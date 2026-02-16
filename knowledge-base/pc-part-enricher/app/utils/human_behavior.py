import asyncio
import random
import math
import app.utils.constants as constants

class HumanBehavior:
    def __init__(self):
        self.total_requests = 0
        self.next_long_break_at = random.randint(30, 60) 

    def _get_gaussian_delay(self, min_d, max_d):
        """
        Генерирует задержку по нормальному распределению (колокол).
        Среднее значение смещено к min_d (люди чаще быстры, чем медленны).
        """
        mean = (min_d + max_d) / 2
        sigma = (max_d - min_d) / 4
        
        delay = random.gauss(mean, sigma)
        return max(min_d * 0.8, min(delay, max_d * 1.2))

    async def wait_before_request(self):
        self.total_requests += 1

        if self.total_requests == 1:
            return

        if self.total_requests >= self.next_long_break_at:
            long_break = self._get_gaussian_delay(
                constants.DELAY_COOLDOWN_LONG_MIN, 
                constants.DELAY_COOLDOWN_LONG_MAX
            )
            print(f"Taking a long break ({long_break:.1f}s) after {self.total_requests} requests...")
            await asyncio.sleep(long_break)
            
            self.next_long_break_at = self.total_requests + random.randint(40, 70)
            return

        if random.random() < 0.08:
            short_break = self._get_gaussian_delay(
                constants.DELAY_COOLDOWN_SHORT_MIN, 
                constants.DELAY_COOLDOWN_SHORT_MAX
            )
            print(f"Short distraction ({short_break:.1f}s)...")
            await asyncio.sleep(short_break)
            return

        base_wait = self._get_gaussian_delay(constants.DELAY_MIN, constants.DELAY_MAX)
        
        await asyncio.sleep(base_wait)