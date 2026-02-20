import os
import pandas as pd
from app.utils.csv_handler import load_csv, save_csv

async def process_part_type(part_type, enricher, input_dir, output_dir, limit=0, start_index=0):
    input_path = os.path.join(input_dir, f'{part_type}.csv')
    output_path = os.path.join(output_dir, f'{part_type}_enriched.csv')

    try:
        df = load_csv(input_path)
    except FileNotFoundError:
        print(f"Skipping '{part_type}': Input file not found.")
        return False
    except Exception as e:
        print(f"Skipping '{part_type}': Error loading CSV - {e}")
        return False

    enriched_rows = []

    total_rows = len(df)
    
    if start_index >= total_rows:
        print(f"Start index {start_index} is larger than total rows {total_rows}. Skipping '{part_type}'.")
        return True

    target_df = df.iloc[start_index:]

    if limit > 0:
        target_df = target_df.head(limit)
    
    rows_to_process = len(target_df)
    print(f"--- Processing: {part_type} (Total: {total_rows}, Starting from: {start_index}, Chunk size: {rows_to_process}) ---")

    for current_idx, (index, row) in enumerate(target_df.iterrows(), start=start_index):
        url = row.get('part_url')
        base_row_dict = row.to_dict()

        if pd.isna(url):
            print(f"Skipping row {current_idx + 1} in {part_type}: url is missing")
            continue
            
        try:
            print(f"[{part_type}] Row {current_idx + 1}/{total_rows}...") 

            enriched_data = await enricher.enrich_part(part_type, url, base_row_dict)

            if enriched_data:
                enriched_rows.append(enriched_data)
            else:
                print(f"Skipping row {current_idx + 1} in {part_type}: enrichment failed")

        except Exception as e:
            print(f"Error processing row {current_idx + 1} in {part_type}: {e}")

    should_append = (start_index > 0)
    save_csv(enriched_rows, output_path, append=should_append)
    
    return True