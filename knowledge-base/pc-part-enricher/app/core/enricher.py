import asyncio
import random
from pypartpicker import Scraper
from app.utils.constants import PARSER_MAP

class PartEnricher:
    def __init__(self):
        self.scraper = Scraper()
        
        self.parser_map = PARSER_MAP

    async def enrich_part(self, part_type, url, base_row):
        # slows down the program so as not to spam PCPartPicker and potentially get IP banned
        await asyncio.sleep(random.uniform(3, 6))
        
        try:
            print(f"Scraping: {url}")
            
            product = await self.scraper.aio_fetch_product(url)
            specs = product.specs
            
            parse_func = self.parser_map.get(part_type)
            if not parse_func:
                print(f"No parser defined for {part_type}")
                return base_row
                
            return parse_func(specs, base_row)
            
        except Exception as e:
            print(f"Error scraping {url}: {e}")
            return base_row