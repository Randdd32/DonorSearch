import re
import pandas as pd
from app.models.dictionaries import Manufacturer, CpuSocket, MotherboardFormFactor, MemoryType, Color
from app.models.motherboard import Motherboard
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import safe_int, safe_bool, parse_separated_string

class MotherboardImporter(BaseImporter):
    def _parse_m2_slots(self, raw_m2_string: str) -> list[dict]:
        slots = parse_separated_string(raw_m2_string)
        parsed =[]
        for slot in slots:
            match = re.match(r'^([\d/]+)\s+(.*)$', slot)
            if match:
                sizes_str, keys = match.groups()
                sizes =[int(s) for s in sizes_str.split('/') if s.isdigit()]
            else:
                sizes =[]
                keys = slot.strip()
            parsed.append({"sizes": sizes, "keys": keys})
        return parsed

    def import_data(self, csv_path: str):
        print(f"Importing Motherboards from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            socket_id = self.get_or_create_dictionary(CpuSocket, row.get('socket'))
            form_factor_id = self.get_or_create_dictionary(MotherboardFormFactor, row.get('form_factor'))
            memory_type_id = self.get_or_create_dictionary(MemoryType, row.get('memory_type'))
            color_id = self.get_or_create_dictionary(Color, row.get('color'))

            additional_tokens =[
                row.get('socket'), row.get('form_factor'), row.get('memory_type'),
                f"{safe_int(row.get('max_memory'))}gb" if row.get('max_memory') else None,
                f"{safe_int(row.get('memory_speed_max'))}mhz" if row.get('memory_speed_max') else None,
                row.get('color')
            ]
            
            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            mobo = Motherboard(
                **base_kwargs,
                socket_id=socket_id,
                form_factor_id=form_factor_id,
                memory_type_id=memory_type_id,
                color_id=color_id,
                
                max_memory_gb=safe_int(row.get('max_memory')),
                memory_slots=safe_int(row.get('memory_slots')),
                memory_speed_max_mhz=safe_int(row.get('memory_speed_max')),
                
                ecc_support=safe_bool(row.get('ecc_support')),
                uses_back_connect=safe_bool(row.get('uses_back_connect')),
                
                m2_slots=self._parse_m2_slots(row.get('m2_slots')),

                sata_6_ports=safe_int(row.get('sata_6_ports')) or 0,
                sata_3_ports=safe_int(row.get('sata_3_ports')) or 0,
                
                pci_x16_slots=safe_int(row.get('pci_x16_slots')) or 0,
                pci_x8_slots=safe_int(row.get('pci_x8_slots')) or 0,
                pci_x4_slots=safe_int(row.get('pci_x4_slots')) or 0,
                pci_x1_slots=safe_int(row.get('pci_x1_slots')) or 0,
                pci_slots=safe_int(row.get('pci_slots')) or 0,
                mini_pcie_msata_slots=safe_int(row.get('mini_pcie_msata_slots')) or 0,

                header_usb_2_0=safe_int(row.get('header_usb_2_0')) or 0,
                header_usb_3_2_gen_1=safe_int(row.get('header_usb_3_2_gen_1')) or 0,
                header_usb_3_2_gen_2=safe_int(row.get('header_usb_3_2_gen_2')) or 0,
                header_usb_3_2_gen_2x2=safe_int(row.get('header_usb_3_2_gen_2x2')) or 0,
                header_usb_2_0_single_port=safe_int(row.get('header_usb_2_0__(single_port)')) or 0
            )

            self.session.add(mobo)
            self.session.flush()
            self.save_part_numbers(mobo.id, part_numbers)

        self.session.commit()
        print("Motherboards imported successfully")