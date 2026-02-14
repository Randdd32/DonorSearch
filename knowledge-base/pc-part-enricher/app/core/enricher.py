import asyncio
import random
import aiohttp
from bs4 import BeautifulSoup
from pypartpicker import Scraper

from camoufox.async_api import AsyncCamoufox
from browserforge.fingerprints import Screen

import app.utils.constants as constants

class PartEnricher:
    def __init__(self, use_browser=False):
        self.scraper = Scraper()
        self.parser_map = constants.PARSER_MAP
        self.use_browser = use_browser

        self.cm = None      
        self.context = None

    async def start_browser(self):
        if self.use_browser:
            print("Launching Camoufox (Firefox Stealth)...")
            
            self.cm = AsyncCamoufox(
                os=["windows", "macos", "linux"],
                humanize=True,
                block_images=False,
                geoip=True,
                screen=Screen(max_width=1920, max_height=1080)
            )
            
            self.context = await self.cm.__aenter__()
            print("Camoufox started successfully")

    async def stop_browser(self):
        if self.cm:
            print("Stopping Camoufox...")
            await self.cm.__aexit__(None, None, None)

    def _parse_html_to_specs(self, html):
        soup = BeautifulSoup(html, 'html.parser')
        
        specs_block = soup.find(class_="block xs-hide md-block specs")
        if not specs_block:
            print('Couldn\'t find specs_block')
            html_lower = html.lower()
            if any(marker in html_lower for marker in constants.CAPTCHA_MARKERS):
                return "CAPTCHA"
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

    async def _fetch_with_camoufox(self, url, retries=3):
        print('Starting browser fetch')
        
        for attempt in range(retries):
            page = await self.context.new_page()
            
            try:
                await asyncio.sleep(random.uniform(constants.DELAY_MIN, constants.DELAY_MAX))
                
                print(f"Browser navigating to: {url} (Attempt {attempt+1}/{retries})")
                
                try:
                    response = await page.goto(url, wait_until='domcontentloaded', timeout=constants.TIMEOUT_PAGE_LOAD)
                except Exception as nav_err:
                    print(f"Navigation error: {nav_err}. Retrying...")
                    await page.close()
                    await asyncio.sleep(constants.DELAY_ERROR_RETRY) 
                    continue

                if response.status == 429:
                    print(f"Browser got 429. Cooling down {constants.DELAY_COOLDOWN_LONG}s...")
                    await page.close()
                    await asyncio.sleep(constants.DELAY_COOLDOWN_LONG)
                    continue
                
                if response.status != 200:
                    print(f"Browser failed to fetch {url}: {response.status}")
                    await page.close()
                    return None

                try:
                    await page.wait_for_selector('.block.xs-hide.md-block.specs', timeout=constants.TIMEOUT_SELECTOR)
                except:
                    pass 

                content = await page.content()
                await page.close()

                result = self._parse_html_to_specs(content)
                
                if result == "CAPTCHA":
                    print(f"CAPTCHA! Waiting ({constants.DELAY_COOLDOWN_LONG}s)...")
                    await asyncio.sleep(constants.DELAY_COOLDOWN_LONG)
                    continue  
                
                return result

            except Exception as e:
                print(f"Browser error for {url}: {e}")
                await page.close()
                await asyncio.sleep(constants.DELAY_ERROR_RETRY)
        
        print(f"Failed to fetch {url} after {retries} attempts")
        return None
        
    async def _fetch_manual_fast(self, url, retries=2):
        print('Starting fast fetch')
        
        headers = {
            'User-Agent': random.choice(cfg.LINUX_USER_AGENTS),
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Accept-Language': constants.LOCALE.lower() + ',en;q=0.5'
        }
        
        for attempt in range(retries):
            try:
                await asyncio.sleep(random.uniform(constants.DELAY_FAST_MIN, constants.DELAY_FAST_MAX))

                async with aiohttp.ClientSession(headers=headers) as session:
                    async with session.get(url) as response:
                        if response.status == 200:
                            html = await response.text()
                            return self._parse_html_to_specs(html)
                        elif response.status == 429:
                            print(f"429 in fast mode. Waiting {constants.DELAY_COOLDOWN_SHORT}s...")
                            await asyncio.sleep(constants.DELAY_COOLDOWN_SHORT)
                            continue
                        else:
                            print(f"Failed to fetch {url} with status {response.status}")
                            return None
            except Exception as e:
                print(f"Fast fetch failed for {url}: {e}")
        return None

    async def enrich_part(self, part_type, url, base_row):
        specs = None

        if self.use_browser:
            specs = await self._fetch_with_camoufox(url)
        else:
            try:
                await asyncio.sleep(random.uniform(constants.DELAY_FAST_MIN, constants.DELAY_FAST_MAX))
                print(f"Scraping: {url}")
                product = await self.scraper.aio_fetch_product(url)
                specs = product.specs
            except Exception as lib_error:
                print(f"Library scrape failed for {url}: {lib_error}")
                specs = await self._fetch_manual_fast(url)

        if not specs:
            print(f"Failed to get specs for {url}")
            return base_row

        parse_func = self.parser_map.get(part_type)
        if not parse_func:
            print(f"No parser defined for {part_type}")
            return base_row 
            
        try:
            return parse_func(specs, base_row)
        except Exception as e:
            print(f"Parse logic error: {e}")
            return base_row