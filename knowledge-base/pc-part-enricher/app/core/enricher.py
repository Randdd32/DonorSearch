import asyncio
import random
import aiohttp
from bs4 import BeautifulSoup
from pypartpicker import Scraper
from camoufox.async_api import AsyncCamoufox
from browserforge.fingerprints import Screen

import app.utils.constants as constants
from app.utils.proxy_manager import ProxyManager
from app.utils.human_behavior import HumanBehavior

class PartEnricher:
    def __init__(self, use_browser=False, docker_mode=False):
        self.scraper = Scraper()
        self.parser_map = constants.PARSER_MAP

        self.use_browser = use_browser
        self.docker_mode = docker_mode

        self.cm = None      
        self.context = None
        self.page = None

        self.proxy_manager = ProxyManager()
        self.current_proxy = None

        self.human = HumanBehavior()

        self.request_count = 0
        self.max_requests_per_session = 250

    async def launch_browser(self, proxy_str=None):
        if not self.use_browser:
            return

        if proxy_str is None:
            proxy_str = self.proxy_manager.get_random_proxy()

        if self.cm:
            try:
                await self.cm.__aexit__(None, None, None)
            except Exception:
                pass

        current_proxy_str = proxy_str
        max_retries = 5
        attempt = 0

        while attempt < max_retries:
            attempt += 1
            
            print(f"Launching Camoufox (Attempt {attempt}/{max_retries}, Proxy: {current_proxy_str if current_proxy_str else 'Direct'})...")
            
            headless_mode = "virtual" if self.docker_mode else False
            
            pw_proxy = self.proxy_manager.parse_playwright_proxy(current_proxy_str) if current_proxy_str else None

            self.cm = AsyncCamoufox(
                proxy=pw_proxy,
                headless=headless_mode,
                os=["windows", "macos", "linux"],
                humanize=True,
                block_images=False,
                geoip=True,
                screen=Screen(max_width=1920, max_height=1080)
            )

            try:
                self.context = await self.cm.__aenter__()
                self.page = await self.context.new_page()
                self.current_proxy = current_proxy_str
                print("Camoufox started successfully")
                return

            except Exception as e:
                print(f"Failed to launch browser with proxy {current_proxy_str}: {e}")
                try:
                    await self.cm.__aexit__(None, None, None)
                except:
                    pass
                
                if current_proxy_str:
                    new_proxy = self.proxy_manager.get_random_proxy(exclude=current_proxy_str)
                    
                    if new_proxy and new_proxy != current_proxy_str:
                        print("Switching to another proxy...")
                        current_proxy_str = new_proxy
                        continue
                    else:
                        print("No other proxies available to retry.")
                        raise e
                else:
                    raise e

        raise Exception("Failed to launch browser after multiple attempts")
    
    async def _rotate_proxy(self):
        proxy_rotation_break = random.uniform(constants.DELAY_PROXY_ROTATION_MIN, constants.DELAY_PROXY_ROTATION_MAX)
        print(f"Cooling down before proxy rotating {proxy_rotation_break}s...")
        await asyncio.sleep(proxy_rotation_break)

        if not self.proxy_manager.proxies:
            print(f"No proxies configured")
            return

        if len(self.proxy_manager.proxies) == 1:
            print(f"Single proxy detected: {self.proxy_manager.proxies[0]}")
            await self.launch_browser(self.proxy_manager.proxies[0])
            return

        new_proxy = self.proxy_manager.get_random_proxy(exclude=self.current_proxy)
        print(f"Rotating proxy: {self.current_proxy} -> {new_proxy}")
        
        await self.launch_browser(new_proxy)

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
        self.request_count += 1
        if self.request_count >= self.max_requests_per_session:
            print(f"Session limit reached ({self.request_count}). Restarting browser to clear cookies...")
            self.request_count = 0
            await self.launch_browser(self.proxy_manager.get_random_proxy(exclude=self.current_proxy))

        await self.human.wait_before_request()
        print('Starting browser fetch') 
        
        for attempt in range(retries):
            if self.page.is_closed():
                self.page = await self.context.new_page()
            
            try:  
                print(f"Browser navigating to: {url} (Attempt {attempt+1}/{retries})")
                
                try:
                    response = await self.page.goto(url, wait_until='domcontentloaded', timeout=constants.TIMEOUT_PAGE_LOAD)
                except Exception as nav_err:
                    print(f"Navigation error: {nav_err}. Rotating...")
                    await self._rotate_proxy()
                    continue

                if response.status == 429:
                    print(f"429 Detected on {self.current_proxy}. Rotating...")
                    await self._rotate_proxy()
                    continue
                
                if response.status != 200:
                    print(f"Browser failed to fetch {url}: {response.status}. Rotating...")
                    await self._rotate_proxy()
                    continue

                try:
                    await self.page.wait_for_selector('.block.xs-hide.md-block.specs', timeout=constants.TIMEOUT_SELECTOR)
                except:
                    pass 

                content = await self.page.content()

                result = self._parse_html_to_specs(content)
                
                if result == "CAPTCHA":
                    print(f"CAPTCHA on {self.current_proxy}! Rotating...")
                    await self._rotate_proxy()
                    continue   
                
                return result

            except Exception as e:
                print(f"Browser error for {url}: {e}. Rotating...")
                await self._rotate_proxy()
        
        print(f"Failed to fetch {url} after {retries} attempts")
        return None
        
    async def _fetch_manual_fast(self, url, retries=2):
        print('Starting fast fetch')
        
        headers = {
            'User-Agent': random.choice(constants.LINUX_USER_AGENTS),
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
                            print(f"429 in fast mode. Waiting {constants.DELAY_COOLDOWN_LONG_MIN}s...")
                            await asyncio.sleep(constants.DELAY_COOLDOWN_LONG_MIN)
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
            return None

        parse_func = self.parser_map.get(part_type)
        if not parse_func:
            print(f"No parser defined for {part_type}")
            return None 
            
        try:
            return parse_func(specs, base_row)
        except Exception as e:
            print(f"Parse logic error: {e}")
            return None