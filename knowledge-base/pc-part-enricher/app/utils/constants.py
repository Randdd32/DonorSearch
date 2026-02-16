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

LOCALE = 'en-US'

LINUX_USER_AGENTS = [
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36",
]

TIMEOUT_PAGE_LOAD = 60000        
TIMEOUT_SELECTOR = 5000          

DELAY_MIN = 3.0
DELAY_MAX = 8.0
DELAY_FAST_MIN = 3.0
DELAY_FAST_MAX = 6.0
      
DELAY_COOLDOWN_SHORT_MIN = 15.0
DELAY_COOLDOWN_SHORT_MAX = 30.0      
DELAY_COOLDOWN_LONG_MIN = 60.0
DELAY_COOLDOWN_LONG_MAX = 300.0 

DELAY_PROXY_ROTATION_MIN = 30.0
DELAY_PROXY_ROTATION_MAX = 300.0

CAPTCHA_MARKERS = ["captcha", "challenge", "verify you are human"]