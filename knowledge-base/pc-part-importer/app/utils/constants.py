import os

from app.importers.optical_drive_importer import OpticalDriveImporter

DB_URL = os.getenv("DB_URL", "postgresql://postgres:postgres@localhost:5432/donor_search_db")

DATA_DIR = os.getenv("DATA_DIR", "./data")

IMPORTER_MAP = {
    'optical-drive': OpticalDriveImporter,
}

ALL_PART_TYPES = sorted(list(IMPORTER_MAP.keys()))