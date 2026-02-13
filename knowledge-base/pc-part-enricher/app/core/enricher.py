import asyncio
import random
import aiohttp
from bs4 import BeautifulSoup
from pypartpicker import Scraper
from fake_useragent import UserAgent
from playwright.async_api import async_playwright

from app.utils.constants import PARSER_MAP

class PartEnricher:
    def __init__(self, use_browser=False):
        self.scraper = Scraper()
        self.ua = UserAgent()
        self.parser_map = PARSER_MAP
        self.use_browser = use_browser

        self.playwright = None
        self.browser = None
        self.context = None

    async def start_browser(self):
        if self.use_browser:
            print("Launching Headless Browser (Playwright)...")
            self.playwright = await async_playwright().start()
            self.browser = await self.playwright.chromium.launch(headless=True)
            self.context = await self.browser.new_context(
                user_agent=self.ua.random,
                viewport={'width': 1920, 'height': 1080}
            )

    async def stop_browser(self):
        if self.browser:
            await self.browser.close()
        if self.playwright:
            await self.playwright.stop()
        print("Browser stopped")

    def _parse_html_to_specs(self, html):
        soup = BeautifulSoup(html, 'html.parser')
        
        specs_block = soup.find(class_="block xs-hide md-block specs")
        if not specs_block:
            print('Couldn\'t find specs_block')
            if "captcha" in html.lower() or "challenge" in html.lower():
                print("CAPTCHA detected in HTML content")
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

    async def _fetch_with_playwright(self, url):
        print('Starting browser fetch')
        page = await self.context.new_page()

        try:   
            response = await page.goto(url, wait_until='domcontentloaded', timeout=60000)
            
            if response.status == 429:
                print(f"Browser got 429. Sleeping for 60s...")
                await asyncio.sleep(60)
                await page.close()
                return None
            
            if response.status != 200:
                print(f"Browser failed to fetch {url}: {response.status}")
                await page.close()
                return None

            try:
                await page.wait_for_selector('.block.xs-hide.md-block.specs', timeout=5000)
            except:
                pass

            content = await page.content()
            await page.close()
            return self._parse_html_to_specs(content)

        except Exception as e:
            print(f"Browser error for {url}: {e}")
            await page.close()
            return None
        
    async def _manual_fetch_specs(self, url):
        print('Starting fast fetch')
        
        headers = {
            'User-Agent': self.ua.random,
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Accept-Language': 'en-US,en;q=0.5'
        }
        
        for attempt in range(retries + 1):
            try:
                await asyncio.sleep(random.uniform(3, 6))

                async with aiohttp.ClientSession(headers=headers) as session:
                    async with session.get(url) as response:
                        if response.status == 200:
                            html = await response.text()
                            return self._parse_html_to_specs(html)
                        elif response.status == 429:
                            print(f"429 in fast mode. Waiting 60s...")
                            await asyncio.sleep(60)
                            continue
                        else:
                            print(f"Failed to fetch {url} with status {response.status}")
                            continue
            except Exception as e:
                print(f"Fast fetch failed for {url}: {e}")
        return None

    async def enrich_part(self, part_type, url, base_row):
        specs = None

        if self.use_browser:
            await asyncio.sleep(random.uniform(6, 10))
            specs = await self._fetch_with_playwright(url)
        else:
            try:
                await asyncio.sleep(random.uniform(3, 6))
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