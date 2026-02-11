from app import parsers

PARSER_MAP = {
    'case': parsers.parse_case,
    'cpu': parsers.parse_cpu,
    'cpu-cooler': parsers.parse_cpu_cooler,
    'motherboard': parsers.parse_motherboard,
    'memory': parsers.parse_memory,
    'internal-hard-drive': parsers.parse_storage,
    'external-hard-drive': parsers.parse_storage,
    'video-card': parsers.parse_video_card,
    'power-supply': parsers.parse_power_supply,
    'case-fan': parsers.parse_case_fan,
    'monitor': parsers.parse_monitor,
    'sound-card': parsers.parse_expansion_card,
    'wired-network-card': parsers.parse_expansion_card,
    'wireless-network-card': parsers.parse_expansion_card,
    'optical-drive': parsers.parse_optical_drive
}

ALL_PART_TYPES = sorted(list(PARSER_MAP.keys()))