import pandas as pd
from app.models.dictionaries import Manufacturer, MonitorResolution, PanelType, AspectRatio
from app.models.monitor import Monitor
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import safe_int, safe_float

class MonitorImporter(BaseImporter):
    def import_data(self, csv_path: str):
        print(f"Importing Monitors from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            res_raw = row.get('resolution')
            resolution_str = str(res_raw).replace(',', 'x').strip() if res_raw and pd.notnull(res_raw) else None

            resolution_id = self.get_or_create_dictionary(MonitorResolution, resolution_str)
            panel_type_id = self.get_or_create_dictionary(PanelType, row.get('panel_type'))
            aspect_ratio_id = self.get_or_create_dictionary(AspectRatio, row.get('aspect_ratio'))

            screen_size = safe_float(row.get('screen_size'))
            refresh_rate = safe_int(row.get('refresh_rate'))
            response_time = safe_float(row.get('response_time'))

            additional_tokens =[
                resolution_str,
                row.get('panel_type'),
                row.get('aspect_ratio'),
                f"{screen_size}\"" if screen_size else None,
                f"{refresh_rate}hz" if refresh_rate else None,
                f"{response_time}ms" if response_time else None
            ]

            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            monitor = Monitor(
                **base_kwargs,
                resolution_id=resolution_id,
                panel_type_id=panel_type_id,
                aspect_ratio_id=aspect_ratio_id,

                screen_size_in=screen_size,
                refresh_rate_hz=refresh_rate,
                response_time_ms=response_time,

                input_hdmi=safe_int(row.get('input_hdmi')) or 0,
                input_dp=safe_int(row.get('input_dp')) or 0,
                input_dvi=safe_int(row.get('input_dvi')) or 0,
                input_vga=safe_int(row.get('input_vga')) or 0,
                input_usb_c=safe_int(row.get('input_usb_c')) or 0,
                input_mini_hdmi=safe_int(row.get('input_mini_hdmi')) or 0,
                input_micro_hdmi=safe_int(row.get('input_micro_hdmi')) or 0,
                input_mini_dp=safe_int(row.get('input_mini_dp')) or 0,
                input_bnc=safe_int(row.get('input_bnc')) or 0,
                input_component=safe_int(row.get('input_component')) or 0,
                input_s_video=safe_int(row.get('input_s_video')) or 0,
                input_virtual_link=safe_int(row.get('input_virtual_link')) or 0
            )

            self.session.add(monitor)
            self.session.flush()
            self.save_part_numbers(monitor.id, part_numbers)

        self.session.commit()
        print("Monitors imported successfully")