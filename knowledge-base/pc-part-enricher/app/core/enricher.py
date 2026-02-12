import asyncio
import random
import aiohttp
from bs4 import BeautifulSoup
from pypartpicker import Scraper
from app.utils.constants import PARSER_MAP

class PartEnricher:
    def __init__(self):
        self.scraper = Scraper()

        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'en-US,en;q=0.5'
        }
        
        self.parser_map = PARSER_MAP

    async def _manual_fetch_specs(self, url):
        try:
            await asyncio.sleep(random.uniform(5, 10))

            async with aiohttp.ClientSession(headers=self.headers) as session:
                print('Starting manual fetch')
                async with session.get(url) as response:
                    if response.status != 200:
                        print(f"Failed to fetch page: {response.status}")
                        return None
                    html = await response.text()
            
            soup = BeautifulSoup(html, 'html.parser')
            
            specs_block = soup.find(class_="block xs-hide md-block specs")
            if not specs_block:
                print('Couldn\'t find specs_block')
                return {}

            specs = {}
            for spec in specs_block.find_all("div", class_="group group--spec"):
                title_el = spec.find("h3", class_="group__title")
                content_el = spec.find("div", class_="group__content")
                
                if title_el and content_el:
                    key = title_el.get_text()
                    value = (
                        content_el.get_text()
                        .strip()
                        .replace("\u00b3", "")
                        .replace('"', "")
                        .split("\n")
                    )
                    specs[key] = value
            print (f"Parsed specs: {specs}")
            return specs

        except Exception as e:
            print(f"Manual fallback failed for {url}: {e}")
            return None

    async def enrich_part(self, part_type, url, base_row):
        # slows down the program so as not to spam PCPartPicker and potentially get IP banned
        await asyncio.sleep(random.uniform(3, 6))
        
        specs = None

        try:
            print(f"Scraping: {url}")
            product = await self.scraper.aio_fetch_product(url)
            specs = product.specs 
        except Exception as lib_error:
            print(f"Library scrape failed for {url}: {lib_error}")
            specs = await self._manual_fetch_specs(url)
        
        if not specs:
            print(f"Failed to scrape specs for: {url}")
            return base_row

        parse_func = self.parser_map.get(part_type)
        if not parse_func:
            print(f"No parser defined for {part_type}")
            return base_row 
            
        try:
            return parse_func(specs, base_row)
        except Exception as parse_error:
            print(f"Parser error for {url}: {parse_error}")
            return base_row