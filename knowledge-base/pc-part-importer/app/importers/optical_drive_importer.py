import pandas as pd
from sqlalchemy.orm import Session
from app.models.dictionaries import Manufacturer, StorageInterface, OpticalDriveFormFactor
from app.models.optical_drive import OpticalDrive, OpticalDrivePartNumber
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import parse_separated_string

class OpticalDriveImporter(BaseImporter):
    
    def import_data(self, csv_path: str):
        print(f"Importing Optical Drives from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            manufacturer_id = self.get_or_create_dictionary(Manufacturer, row.get('manufacturer'))
            interface_id = self.get_or_create_dictionary(StorageInterface, row.get('interface'))
            form_factor_id = self.get_or_create_dictionary(OpticalDriveFormFactor, row.get('form_factor'))

            part_numbers_list = parse_separated_string(row.get('part_number'))

            search_tokens =[
                str(row.get('manufacturer', '')),
                str(row.get('name', '')),
                str(row.get('form_factor', '')),
                str(row.get('interface', '')),
                " ".join(part_numbers_list)
            ]
            
            if row.get('bd'): search_tokens.append(f"bd-{int(float(row['bd']))}x")
            if row.get('dvd'): search_tokens.append(f"dvd-{int(float(row['dvd']))}x")
            if row.get('cd'): search_tokens.append(f"cd-{int(float(row['cd']))}x")

            clean_tokens =[t.strip().lower() for t in search_tokens if t and t.strip() and t.lower() != 'none']
            search_name = " ".join(clean_tokens)

            drive = OpticalDrive(
                name=row.get('name'),
                search_name=search_name,
                manufacturer_id=manufacturer_id,
                form_factor_id=form_factor_id,
                interface_id=interface_id
            )
            self.session.add(drive)
            self.session.flush()

            for pn in part_numbers_list:
                if pn.strip():
                    pn_record = OpticalDrivePartNumber(
                        optical_drive_id=drive.id,
                        part_number=pn.strip()
                    )
                    self.session.add(pn_record)

        self.session.commit()
        print("Optical drives imported successfully")