import pandas as pd
from app.models.dictionaries import Manufacturer, Color, MemoryType, RamFormFactor
from app.models.memory import Memory
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import safe_int, safe_bool

class MemoryImporter(BaseImporter):
    def import_data(self, csv_path: str):
        print(f"Importing RAM from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            form_factor_id = self.get_or_create_dictionary(RamFormFactor, row.get('form_factor'))
            memory_type_id = self.get_or_create_dictionary(MemoryType, row.get('memory_type'))
            color_id = self.get_or_create_dictionary(Color, row.get('color'))

            mod_count = safe_int(row.get('modules_count'))
            mod_size = safe_int(row.get('modules_size'))
            freq = safe_int(row.get('frequency'))
            cas = safe_int(row.get('cas_latency'))

            additional_tokens =[
                row.get('memory_type'),
                row.get('color'),
                f"{mod_count}x{mod_size}gb",
                f"{mod_count * mod_size}gb",
                f"{freq}mhz" if freq else None,
                f"cl{cas}" if cas else None
            ]

            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            ram = Memory(
                **base_kwargs,
                form_factor_id=form_factor_id,
                memory_type_id=memory_type_id,
                color_id=color_id,
                
                cas_latency=cas,
                frequency_mhz=freq,
                modules_count=mod_count,
                modules_size_gb=mod_size,
                
                is_ecc=safe_bool(row.get('is_ecc')),
                is_registered=safe_bool(row.get('is_registered'))
            )

            self.session.add(ram)
            self.session.flush()
            self.save_part_numbers(ram.id, part_numbers)

        self.session.commit()
        print("RAM imported successfully")