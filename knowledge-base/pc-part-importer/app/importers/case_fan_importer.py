import pandas as pd
from app.models.dictionaries import Manufacturer, Color, FanConnector
from app.models.case_fan import CaseFan
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import parse_separated_string, safe_int, safe_bool, parse_range

class CaseFanImporter(BaseImporter):
    def import_data(self, csv_path: str):
        print(f"Importing Case Fans from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            color_id = self.get_or_create_dictionary(Color, row.get('color'))
            
            connector_entities = self.get_m2m_entities(FanConnector, row.get('connectors'))
            connector_strs = parse_separated_string(row.get('connectors'))
            
            rpm_min, rpm_max = parse_range(row.get('rpm'))
            airflow_min, airflow_max = parse_range(row.get('airflow'))
            size = safe_int(row.get('size'))
            
            additional_tokens =[
                row.get('color'),
                f"{size}mm" if size else None,
                "pwm" if safe_bool(row.get('pwm')) else None,
                f"{rpm_max}rpm" if rpm_max else None,
                f"{airflow_max}cfm" if airflow_max else None,
                " ".join(connector_strs)
            ]
            
            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            fan = CaseFan(
                **base_kwargs,
                color_id=color_id,
                size_mm=size,
                pwm=safe_bool(row.get('pwm')),
                rpm_min=rpm_min,
                rpm_max=rpm_max,
                airflow_min=airflow_min,
                airflow_max=airflow_max,
                connectors=connector_entities
            )

            self.session.add(fan)
            self.session.flush()
            self.save_part_numbers(fan.id, part_numbers)

        self.session.commit()
        print("Case Fans imported successfully")