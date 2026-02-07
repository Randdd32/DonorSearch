import asyncio
import random
from pypartpicker2 import Scraper
import parsers

class PartEnricher:
    def __init__(self):
        self.scraper = Scraper()
        
        self.parser_map = {
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

    async def enrich_part(self, part_type, url):
        # slows down the program so as not to spam PCPartPicker and potentially get IP banned
        await asyncio.sleep(random.uniform(3, 6))
        
        try:
            print(f"Scraping: {url}")
            
            product = await self.scraper.aio_fetch_product(url)
            specs = product.specs
            
            parse_func = self.parser_map.get(part_type)
            if not parse_func:
                print(f"No parser defined for {part_type}")
                return None
                
            return parse_func(specs)
            
        except Exception as e:
            print(f"Error scraping {url}: {e}")
            return None