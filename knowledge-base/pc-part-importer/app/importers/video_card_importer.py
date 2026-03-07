import pandas as pd
from app.models.dictionaries import GpuChipset, MemoryType, ExpansionInterface, Color
from app.models.video_card import VideoCard
from app.importers.base_importer import BaseImporter
from app.utils import safe_int

class VideoCardImporter(BaseImporter):
    def import_data(self, csv_path: str):
        print(f"Importing Video Cards from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            chipset_id = self.get_or_create_dictionary(GpuChipset, row.get('chipset'))
            memory_type_id = self.get_or_create_dictionary(MemoryType, row.get('memory_type'))
            interface_id = self.get_or_create_dictionary(ExpansionInterface, row.get('interface'))
            color_id = self.get_or_create_dictionary(Color, row.get('color'))

            part_numbers_list = parse_separated_string(row.get('part_number'))

            additional_tokens =[
                row.get('chipset'), f"{safe_int(row.get('memory'))}gb" if row.get('memory') else None,
                row.get('memory_type'), f"{safe_int(row.get('core_clock'))}mhz" if row.get('core_clock') else None,
                f"{safe_int(row.get('boost_clock'))}mhz" if row.get('boost_clock') else None, row.get('color')
            ]

            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            video_outputs_json = {}
            for col in df.columns:
                if col.endswith('_outputs'):
                    val = safe_int(row.get(col))
                    if val is not None and val > 0:
                        video_outputs_json[col.replace('_outputs', '')] = val

            gpu = VideoCard(
                **base_kwargs,
                chipset_id=chipset_id,
                memory_type_id=memory_type_id,
                interface_id=interface_id,
                color_id=color_id,
                
                memory_gb=safe_int(row.get('memory')),
                length_mm=safe_int(row.get('length')),
                tdp_w=safe_int(row.get('tdp')),
                slot_width=safe_int(row.get('slot_width')),
                case_expansion_width=safe_int(row.get('case_expansion_width')),

                power_6pin_count=safe_int(row.get('power_6pin_count')) or 0,
                power_8pin_count=safe_int(row.get('power_8pin_count')) or 0,
                power_12pin_count=safe_int(row.get('power_12pin_count')) or 0,
                power_12vhpwr_count=safe_int(row.get('power_12vhpwr_count')) or 0,
                power_eps_count=safe_int(row.get('power_eps_count')) or 0,

                video_outputs=video_outputs_json
            )

            self.session.add(gpu)
            self.session.flush()

            self.save_part_numbers(gpu.id, part_numbers)

        self.session.commit()
        print("Video Cards imported successfully")