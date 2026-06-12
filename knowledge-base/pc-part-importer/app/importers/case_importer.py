import pandas as pd
import re
from app.models.dictionaries import Manufacturer, Color, CaseType, SidePanel, MotherboardFormFactor, FrontPanelUsb
from app.models.case import Case
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import parse_separated_string, safe_int

class CaseImporter(BaseImporter):
    def _extract_sizes_mm(self, text: str) -> list[int]:
        if pd.isna(text) or not str(text).strip():
            return []
        
        # Ищем 2-3 значные числа, после которых не идет 'x'
        pattern = r'\b(\d{2,3})\b(?!\s*x)'
        matches = re.findall(pattern, str(text), flags=re.IGNORECASE)
        
        unique_sizes = set(int(m) for m in matches)
        return sorted(unique_sizes)

    def import_data(self, csv_path: str):
        print(f"Importing Cases from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            type_id = self.get_or_create_dictionary(CaseType, row.get('type'))
            color_id = self.get_or_create_dictionary(Color, row.get('color'))
            side_panel_id = self.get_or_create_dictionary(SidePanel, row.get('side_panel'))

            mobo_entities = self.get_m2m_entities(MotherboardFormFactor, row.get('mobo_form_factor'))
            usb_entities = self.get_m2m_entities(FrontPanelUsb, row.get('front_panel_usb'))

            additional_tokens =[row.get('type'), row.get('color'), row.get('side_panel')]
            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            width = safe_int(row.get('width'))
            cooler_height = safe_int(row.get('max_cpu_cooler_height'))
            if cooler_height is None and width is not None:
                cooler_height = int(width * 0.78)

            pc_case = Case(
                **base_kwargs,
                type_id=type_id,
                color_id=color_id,
                side_panel_id=side_panel_id,
                
                length_mm=safe_int(row.get('length')),
                width_mm=width,
                height_mm=safe_int(row.get('height')),
                max_gpu_len_mm=safe_int(row.get('max_gpu_len')),
                max_cpu_cooler_height_mm=cooler_height,

                int_35_bays=safe_int(row.get('int_35_bays')) or 0,
                ext_525_bays=safe_int(row.get('ext_525_bays')) or 0,
                ext_35_bays=safe_int(row.get('ext_35_bays')) or 0,
                int_25_bays=safe_int(row.get('int_25_bays')) or 0,

                expansion_slots_full_height=safe_int(row.get('expansion_slots_full_height')) or 0,
                expansion_slots_half_height=safe_int(row.get('expansion_slots_half_height')) or 0,
                expansion_slots_riser=safe_int(row.get('expansion_slots_riser')) or 0,

                radiator_support_text=row.get('radiator_support'),
                fan_support_text=row.get('fan_support'),
                
                radiator_sizes=self._extract_sizes_mm(row.get('radiator_support')),
                fan_sizes=self._extract_sizes_mm(row.get('fan_support')),

                mobo_form_factors=mobo_entities,
                front_panel_usbs=usb_entities
            )

            self.session.add(pc_case)
            self.session.flush()
            self.save_part_numbers(pc_case.id, part_numbers)

        self.session.commit()
        print("Cases imported successfully")