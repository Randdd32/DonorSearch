import pandas as pd
from app.models.dictionaries import Manufacturer, Color, CpuSocket
from app.models.cpu_cooler import CpuCooler
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import safe_int, safe_bool, parse_range, parse_separated_string

class CpuCoolerImporter(BaseImporter):
    def import_data(self, csv_path: str):
        print(f"Importing CPU Coolers from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            color_id = self.get_or_create_dictionary(Color, row.get('color'))
            
            socket_entities = self.get_m2m_entities(CpuSocket, row.get('supported_sockets'))
            
            rpm_min, rpm_max = parse_range(row.get('rpm'))
            is_water = safe_bool(row.get('is_water_cooled'))
            height = safe_int(row.get('height'))
            water_size = safe_int(row.get('water_cooled_size'))

            additional_tokens =[
                row.get('color'),
                "water cooled aio liquid" if is_water else "air cooled",
                f"{height}mm" if height else None,
                f"{water_size}mm" if water_size else None,
                f"{rpm_max}rpm" if rpm_max else None,
                " ".join(parse_separated_string(row.get('supported_sockets')))
            ]

            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            cooler = CpuCooler(
                **base_kwargs,
                color_id=color_id,
                is_water_cooled=is_water,
                height_mm=height,
                water_cooled_size_mm=water_size,
                rpm_min=rpm_min,
                rpm_max=rpm_max,
                sockets=socket_entities
            )

            self.session.add(cooler)
            self.session.flush()
            self.save_part_numbers(cooler.id, part_numbers)

        self.session.commit()
        print("CPU Coolers imported successfully")