import pandas as pd
from app.models.dictionaries import Manufacturer, Color, PowerSupplyType, EfficiencyRating, ModularType
from app.models.power_supply import PowerSupply
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import safe_int

class PowerSupplyImporter(BaseImporter):
    def import_data(self, csv_path: str):
        print(f"Importing Power Supplies from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            type_id = self.get_or_create_dictionary(PowerSupplyType, row.get('type'))
            efficiency_id = self.get_or_create_dictionary(EfficiencyRating, row.get('efficiency'))
            color_id = self.get_or_create_dictionary(Color, row.get('color'))
            
            mod_val = row.get('modular')
            if str(mod_val).strip().lower() == 'false':
                mod_val = 'Non-modular'
            modular_id = self.get_or_create_dictionary(ModularType, mod_val)
            
            wattage = safe_int(row.get('wattage'))
            
            additional_tokens =[
                row.get('type'), row.get('efficiency'), mod_val, row.get('color'),
                f"{wattage}w" if wattage else None
            ]

            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            psu = PowerSupply(
                **base_kwargs,
                type_id=type_id,
                efficiency_id=efficiency_id,
                modular_id=modular_id,
                color_id=color_id,
                
                wattage_w=wattage,
                length_mm=safe_int(row.get('length')),

                atx_4pin_connectors=safe_int(row.get('atx_4_pin_connectors')) or 0,
                eps_8pin_connectors=safe_int(row.get('eps_8_pin_connectors')) or 0,
                
                pcie_12vhpwr_connectors=safe_int(row.get('pcie_16_pin_12vhpwr_12v_2x6_connectors')) or 0,
                pcie_12pin_connectors=safe_int(row.get('pcie_12_pin_connectors')) or 0,
                pcie_8pin_connectors=safe_int(row.get('pcie_8_pin_connectors')) or 0,
                pcie_6plus2pin_connectors=safe_int(row.get('pcie_6_2_pin_connectors')) or 0,
                pcie_6pin_connectors=safe_int(row.get('pcie_6_pin_connectors')) or 0,
                
                sata_connectors=safe_int(row.get('sata_connectors')) or 0,
                molex_4pin_connectors=safe_int(row.get('amp_molex_4_pin_connectors')) or 0
            )

            self.session.add(psu)
            self.session.flush()
            self.save_part_numbers(psu.id, part_numbers)

        self.session.commit()
        print("Power Supplies imported successfully")