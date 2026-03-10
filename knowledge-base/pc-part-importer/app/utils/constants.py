import os

from app.importers.optical_drive_importer import OpticalDriveImporter
from app.importers.video_card_importer import VideoCardImporter
from app.importers.case_importer import CaseImporter
from app.importers.power_supply_importer import PowerSupplyImporter
from app.importers.case_fan_importer import CaseFanImporter
from app.importers.cpu_importer import CpuImporter
from app.importers.cpu_cooler_importer import CpuCoolerImporter
from app.importers.storage_importer import StorageImporter
from app.importers.memory_importer import MemoryImporter

DB_URL = os.getenv("DB_URL", "postgresql://postgres:postgres@localhost:5432/donor_search_db")

DATA_DIR = os.getenv("DATA_DIR", "./data")

IMPORTER_MAP = {
    'optical-drive': (OpticalDriveImporter, 'import_data'),
    'video-card': (VideoCardImporter, 'import_data'),
    'case': (CaseImporter, 'import_data'),
    'power-supply': (PowerSupplyImporter, 'import_data'),
    'case-fan': (CaseFanImporter, 'import_data'),
    'cpu': (CpuImporter, 'import_data'),
    'cpu-cooler': (CpuCoolerImporter, 'import_data'),
    'internal-hard-drive': (StorageImporter, 'import_internal'),
    'external-hard-drive': (StorageImporter, 'import_external'),
    'memory': (MemoryImporter, 'import_data'),
    'monitor': (MonitorImporter, 'import_data'),
}

ALL_PART_TYPES = sorted(list(IMPORTER_MAP.keys()))