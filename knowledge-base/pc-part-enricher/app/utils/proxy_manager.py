import os
import random
from dotenv import load_dotenv

load_dotenv()

class ProxyManager:
    def __init__(self):
        proxies_str = os.getenv("PROXIES", "")
        self.proxies = [p.strip() for p in proxies_str.split(',') if p.strip()]
        
    def get_random_proxy(self, exclude=None):
        """Возвращает случайный прокси, исключая указанный (если есть)."""
        if not self.proxies:
            return None

        if len(self.proxies) == 1:
            return self.proxies[0]

        candidates = self.proxies
        if exclude:
            candidates = [p for p in self.proxies if p != exclude]
            
        return random.choice(candidates)

    def parse_playwright_proxy(self, proxy_str):
        """
        Превращает строку протокол://user:pass@host:port в конфиг для Playwright.
        Поддерживает http, https, socks5.
        """
        if not proxy_str:
            return None
            
        if "://" in proxy_str:
            scheme, remainder = proxy_str.split("://", 1)
        else:
            scheme = "http" 
            remainder = proxy_str
        
        try:
            if "@" in remainder:
                auth, host_port = remainder.split("@")
                
                if ":" in auth:
                    username, password = auth.split(":", 1)
                else:
                    username = auth
                    password = ""
                
                return {
                    "server": f"{scheme}://{host_port}",
                    "username": username,
                    "password": password
                }
            else:
                return {"server": f"{scheme}://{remainder}"}
                
        except Exception as e:
            print(f"Error parsing proxy '{proxy_str}': {e}")
            return None