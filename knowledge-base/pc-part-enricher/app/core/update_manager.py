import os
import pandas as pd
from app.core.processor import process_part_type
from app.core.enricher import PartEnricher

class UpdateManager:
    def __init__(self, staging_dir, output_dir, cache_dir='/data/cache', master_dir='/data/master'):
        self.staging_dir = staging_dir  # Входящие файлы от TS (/data/input)
        self.output_dir = output_dir    # Временная папка для результатов парсинга
        self.cache_dir = cache_dir      # История обработанных ссылок (предыдущие результаты от TS)
        self.master_dir = master_dir    # Финальная база без ссылок
        self.delta_dir = '/data/delta'  # Временная папка для новых записей

        for d in [self.cache_dir, self.master_dir, self.delta_dir]:
            os.makedirs(d, exist_ok=True)

    def _get_delta(self, part_type):
        """
        Сравнивает Staging (новый) и Cache (старый).
        Возвращает DataFrame с новыми строками.
        """
        staging_path = os.path.join(self.staging_dir, f"{part_type}.csv")
        cache_path = os.path.join(self.cache_dir, f"{part_type}_history.csv")

        if not os.path.exists(staging_path):
            print(f"Staging file not found: {staging_path}")
            return None

        df_staging = pd.read_csv(staging_path)
        
        if not os.path.exists(cache_path):
            print(f"No history for {part_type}. Treating all as new")
            return df_staging

        try:
            df_cache = pd.read_csv(cache_path)
        except pd.errors.EmptyDataError:
            print(f"History for {part_type} is empty. Treating all as new")
            return df_staging

        if 'part_url' not in df_staging.columns or 'part_url' not in df_cache.columns:
            print(f"Error: 'part_url' missing in CSV for {part_type}")
            return None

        existing_urls = set(df_cache['part_url'].dropna())
        df_delta = df_staging[~df_staging['part_url'].isin(existing_urls)]
        
        return df_delta

    def _update_cache(self, part_type, df_new_items):
        """
        Добавляет новые "сырые" строки в историю, чтобы не парсить их в следующий раз.
        """
        cache_path = os.path.join(self.cache_dir, f"{part_type}_history.csv")
        
        if os.path.exists(cache_path):
            df_history = pd.read_csv(cache_path)
            df_updated = pd.concat([df_history, df_new_items], ignore_index=True)
        else:
            df_updated = df_new_items
            
        df_updated.to_csv(cache_path, index=False)

    def _update_master(self, part_type):
        """
        Добавляет новые обогащенные данные в итоговый файл.
        """
        new_enriched_path = os.path.join(self.output_dir, f"{part_type}_enriched.csv")
        master_path = os.path.join(self.master_dir, f"{part_type}_enriched.csv")
        
        if not os.path.exists(new_enriched_path):
            return

        df_new = pd.read_csv(new_enriched_path)
        
        if os.path.exists(master_path):
            df_master = pd.read_csv(master_path)
            df_final = pd.concat([df_master, df_new], ignore_index=True)
        else:
            df_final = df_new
            
        df_final.to_csv(master_path, index=False)
        print(f"Master csv updated for {part_type}. Total rows: {len(df_final)}")

    async def run_update(self, part_types, args):
        tasks_to_enrich = []
        delta_dfs = {}

        print("=== Step 1: Calculating delta ===")
        for part in part_types:
            df_delta = self._get_delta(part)
            
            if df_delta is not None and not df_delta.empty:
                print(f"Found {len(df_delta)} new items for {part}")
                delta_path = os.path.join(self.delta_dir, f"{part}.csv")
                df_delta.to_csv(delta_path, index=False)
                
                tasks_to_enrich.append(part)
                delta_dfs[part] = df_delta
            else:
                print(f"No new items for {part}")

        if not tasks_to_enrich:
            print("Nothing to update")
            return

        print("\n=== Step 2: Enriching delta ===")

        enricher = PartEnricher(
            use_browser=args.use_browser,
            docker_mode=args.docker_mode
        )
        await enricher.launch_browser()
        
        try:
            for part in tasks_to_enrich:
                await process_part_type(
                    part_type=part, 
                    enricher=enricher, 
                    input_dir=self.delta_dir,
                    output_dir=self.output_dir,
                    limit=args.limit
                )
        finally:
            await enricher.stop_browser()

        print("\n=== Step 3: Committing changes ===")
        for part in tasks_to_enrich:
            self._update_master(part)
            self._update_cache(part, delta_dfs[part])