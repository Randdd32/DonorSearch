import os
from app.database import init_db, SessionLocal
from app.importers.optical_drive_importer import OpticalDriveImporter

DATA_DIR = os.getenv("DATA_DIR", "./data")

def main():
    print("Initializing database...")
    init_db()

    session = SessionLocal()
    try:        
        od_csv = os.path.join(DATA_DIR, 'optical-drive_enriched.csv')
        if os.path.exists(od_csv):
            importer = OpticalDriveImporter(session)
            importer.import_data(od_csv)
        else:
            print(f"File {od_csv} not found.")
        
    except Exception as e:
        session.rollback()
        print(f"An error occurred: {e}")
    finally:
        session.close()

if __name__ == "__main__":
    main()