import pandas as pd
from app.models.dictionaries import StorageInterface, OpticalDriveFormFactor
from app.models.optical_drive import OpticalDrive
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import safe_int

class OpticalDriveImporter(BaseImporter):
    def import_data(self, csv_path: str):
        print(f"Importing Optical Drives from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            interface_id = self.get_or_create_dictionary(StorageInterface, row.get('interface'))
            form_factor_id = self.get_or_create_dictionary(OpticalDriveFormFactor, row.get('form_factor'))

            additional_tokens =[
                row.get('form_factor'), row.get('interface'),
                f"bd-{safe_int(row.get('bd'))}x" if row.get('bd') else None,
                f"dvd-{safe_int(row.get('dvd'))}x" if row.get('dvd') else None,
                f"cd-{safe_int(row.get('cd'))}x" if row.get('cd') else None
            ]

            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            drive = OpticalDrive(
                **base_kwargs,
                form_factor_id=form_factor_id,
                interface_id=interface_id
            )
            self.session.add(drive)
            self.session.flush() 

            self.save_part_numbers(drive.id, part_numbers)

        self.session.commit()
        print("Optical Drives imported successfully")