import asyncio
import sys
from app.core.enricher import PartEnricher
from app.cli import setup_arg_parser, validate_args
from app.core.processor import process_part_type
from app.utils.constants import ALL_PART_TYPES

async def main():
    parser = setup_arg_parser()
    args = parser.parse_args()

    targets = validate_args(args)
    if not targets:
        print("Error: Please specify part types or use --all")
        print(f"Supported types: {', '.join(ALL_PART_TYPES)}")
        sys.exit(1)
    
    enricher = PartEnricher()
    print(f"Queue: {', '.join(targets)}")
    
    for part in targets:
        await process_part_type(
            part_type=part, 
            enricher=enricher, 
            input_dir=args.input, 
            output_dir=args.output, 
            limit=args.limit
        )

    print("\nAll tasks completed.")

if __name__ == "__main__":
    asyncio.run(main())