import pandas as pd
from app.models.dictionaries import Manufacturer, CpuSocket, Microarchitecture, IntegratedGraphics
from app.models.cpu import Cpu
from app.importers.base_importer import BaseImporter
from app.utils.parsing_utils import safe_int, safe_float, safe_bool

class CpuImporter(BaseImporter):
    def import_data(self, csv_path: str):
        print(f"Importing CPUs from {csv_path}...")
        df = pd.read_csv(csv_path)
        df = df.where(pd.notnull(df), None)

        for _, row in df.iterrows():
            socket_id = self.get_or_create_dictionary(CpuSocket, row.get('socket'))
            microarchitecture_id = self.get_or_create_dictionary(Microarchitecture, row.get('microarchitecture'))
            graphics_id = self.get_or_create_dictionary(IntegratedGraphics, row.get('graphics'))

            core_count = safe_int(row.get('core_count'))
            core_clock = safe_float(row.get('core_clock'))
            boost_clock = safe_float(row.get('boost_clock'))

            additional_tokens =[
                row.get('socket'),
                row.get('microarchitecture'),
                row.get('graphics'),
                f"{core_count} core" if core_count else None,
                f"{core_clock}ghz" if core_clock else None,
                f"{boost_clock}ghz" if boost_clock else None
            ]

            base_kwargs, part_numbers = self.build_base_component(row, additional_tokens)

            cpu = Cpu(
                **base_kwargs,
                socket_id=socket_id,
                microarchitecture_id=microarchitecture_id,
                graphics_id=graphics_id,
                
                core_count=core_count,
                core_clock_ghz=core_clock,
                boost_clock_ghz=boost_clock,
                tdp_w=safe_int(row.get('tdp')),
                max_memory_gb=safe_int(row.get('max_memory')),
                ecc_support=safe_bool(row.get('ecc_support'))
            )

            self.session.add(cpu)
            self.session.flush()
            self.save_part_numbers(cpu.id, part_numbers)

        self.session.commit()
        print("CPUs imported successfully")