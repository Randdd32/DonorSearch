import pandas as pd
from models.dictionaries import Manufacturer, Color, StorageType, StorageFormFactor, StorageInterface
from models.storage import Storage
from importers.base_importer import BaseImporter
from app.utils.parsing_utils import safe_int, parse_separated_string

class StorageImporter(BaseImporter):
    def import_internal(self, csv_path: str):
        print(f"Importing Internal Storages from {csv_path}...")
        self._import_data(csv_path, is_external=False)

    def import_external(self, csv_path: str):
        print(f"Importing External Storages from {csv_path}...")
        self._import_data(csv_path, is_external=True)

    def _import_data(self, csv_path: str, is_external: bool):
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            type_id = self.get_or_create_dictionary(StorageType, row.get('type'))
            form_factor_id = self.get_or_create_dictionary(StorageFormFactor, row.get('form_factor'))
            color_id = self.get_or_create_dictionary(Color, row.get('color'))

            interface_entities = self.get_m2m_entities(StorageInterface, row.get('interface'), separator=',')
            interfaces_for_search = " ".join([e.name for e in interface_entities])
            
            capacity = safe_int(row.get('capacity'))

            additional_tokens =[
                row.get('type'), row.get('form_factor'), row.get('color'),
                "internal" if not is_external else None,
                f"{capacity}gb" if capacity else None,
                f"{safe_int(row.get('rpm'))}rpm" if row.get('rpm') else None,
                interfaces_for_search
            ]

            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            storage = Storage(
                **base_kwargs,
                type_id=type_id,
                form_factor_id=form_factor_id,
                color_id=color_id,
                is_external=is_external,
                capacity_gb=capacity,
                cache_mb=safe_int(row.get('cache')),
                rpm=safe_int(row.get('rpm')),
                interfaces=interface_entities
            )

            self.session.add(storage)
            self.session.flush()
            self.save_part_numbers(storage.id, part_numbers)

        self.session.commit()
        print(f"{'External' if is_external else 'Internal'} Storages imported successfully")