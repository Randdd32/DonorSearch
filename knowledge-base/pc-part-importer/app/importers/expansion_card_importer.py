import pandas as pd
from app.models.dictionaries import Manufacturer, ExpansionInterface, Color, AudioChipset, WirelessProtocol
from app.models.expansion_card import ExpansionCard
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import safe_int, safe_float

class ExpansionCardImporter(BaseImporter):
    def import_sound(self, csv_path: str):
        print(f"Importing Sound Cards from {csv_path}...")
        self._import_data(csv_path, "SOUND")

    def import_wired(self, csv_path: str):
        print(f"Importing Wired Network Cards from {csv_path}...")
        self._import_data(csv_path, "WIRED_NETWORK")

    def import_wireless(self, csv_path: str):
        print(f"Importing Wireless Network Cards from {csv_path}...")
        self._import_data(csv_path, "WIRELESS_NETWORK")

    def _import_data(self, csv_path: str, card_type: str):
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            interface_id = self.get_or_create_dictionary(ExpansionInterface, row.get('interface'))
            color_id = self.get_or_create_dictionary(Color, row.get('color'))
            
            audio_chipset_id = None
            protocol_id = None
            channels = None
            digital_audio_bit = None
            sample_rate_khz = None

            additional_tokens =[
                card_type.replace('_', ' '), 
                row.get('interface'), 
                row.get('color')
            ]

            if card_type == "SOUND":
                audio_chipset_id = self.get_or_create_dictionary(AudioChipset, row.get('chipset'))
                channels = safe_float(row.get('channels'))
                digital_audio_bit = safe_int(row.get('digital_audio'))
                sample_rate_khz = safe_float(row.get('sample_rate'))
                
                additional_tokens.extend([
                    row.get('chipset'),
                    f"{channels} channels" if channels else None,
                    f"{digital_audio_bit}bit" if digital_audio_bit else None,
                    f"{sample_rate_khz}khz" if sample_rate_khz else None
                ])
            elif card_type == "WIRELESS_NETWORK":
                protocol_id = self.get_or_create_dictionary(WirelessProtocol, row.get('protocol'))
                additional_tokens.append(row.get('protocol'))

            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            card = ExpansionCard(
                **base_kwargs,
                card_type=card_type,
                interface_id=interface_id,
                color_id=color_id,
                audio_chipset_id=audio_chipset_id,
                channels=channels,
                digital_audio_bit=digital_audio_bit,
                sample_rate_khz=sample_rate_khz,
                protocol_id=protocol_id
            )

            self.session.add(card)
            self.session.flush()
            self.save_part_numbers(card.id, part_numbers)

        self.session.commit()
        print(f"Expansion Cards ({card_type}) imported successfully")