import os
import sys
from app.database import init_db, SessionLocal
from app.utils.constants import IMPORTER_MAP
from app.cli import setup_arg_parser, validate_args

def main():
    parser = setup_arg_parser()
    args = parser.parse_args()

    parts_to_process = validate_args(args)
    if not parts_to_process:
        print("Error: No valid part types to process. Exiting.")
        sys.exit(1)

    data_dir = args.dir

    if args.update:
        print("==================================================")
        print(" MODE: UPDATE (Delta load)")
        print(f" DIR:  {data_dir}")
        print(" Skipping database schema initialization...")
        print("==================================================")
    else:
        print("==================================================")
        print(" MODE: FULL LOAD (Initialization)")
        print(f" DIR:  {data_dir}")
        print(" Initializing database schema and extensions...")
        print("==================================================")
        init_db()

    for part_type in parts_to_process:
        csv_path = os.path.join(data_dir, f"{part_type}_enriched.csv")
        
        if not os.path.exists(csv_path):
            print(f"Skipping '{part_type}': file not found ({csv_path})")
            continue
            
        print(f"Processing '{part_type}'...")
        importer_class, target_method = IMPORTER_MAP[part_type]
        session = SessionLocal()
        
        try:
            importer = importer_class(session)
            import_func = getattr(importer, target_method)
            import_func(csv_path)
        except Exception as e:
            session.rollback()
            print(f" Failed importing '{part_type}': {e}")
        finally:
            session.close()

    print("Process completed!")

if __name__ == "__main__":
    main()