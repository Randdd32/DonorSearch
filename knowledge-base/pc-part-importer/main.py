import os
from app.database import init_db, SessionLocal
from app.importers.optical_drive_importer import OpticalDriveImporter
from app.utils.constants import DATA_DIR, IMPORTER_MAP, ALL_PART_TYPES

def main():
    print("Initializing database...")
    init_db()

    for part_type in ALL_PART_TYPES:
        csv_path = os.path.join(DATA_DIR, f"{part_type}_enriched.csv")
        
        if not os.path.exists(csv_path):
            print(f"Skipping '{part_type}': file not found ({csv_path})")
            continue
            
        importer_class = IMPORTER_MAP[part_type]
        session = SessionLocal()
        
        try:
            importer = importer_class(session)
            importer.import_data(csv_path)
        except Exception as e:
            session.rollback()
            print(f"Error importing '{part_type}': {e}")
        finally:
            session.close()

if __name__ == "__main__":
    main()