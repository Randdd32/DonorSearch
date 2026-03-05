import os

from app import importers

DB_URL = os.getenv("DB_URL", "postgresql://postgres:postgres@localhost:5432/donor_search_db")

DATA_DIR = os.getenv("DATA_DIR", "./data")

IMPORTER_MAP = {
    'optical-drive': importers.OpticalDriveImporter,
    'case': importers.CaseImporter,
    'cpu': importers.CPUImporter,
    'cpu-cooler': importers.CPUCoolerImporter,
    'motherboard': importers.MotherboardImporter,
    'memory': importers.MemoryImporter,
    'video-card': importers.VideoCardImporter,
    'power-supply': importers.PowerSupplyImporter,
    'case-fan': importers.CaseFanImporter,
    'monitor': importers.MonitorImporter
}

ALL_PART_TYPES = sorted(list(IMPORTER_MAP.keys()))