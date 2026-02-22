import asyncio
import sys
from app.core.enricher import PartEnricher
from app.cli import setup_arg_parser, validate_args
from app.core.processor import process_part_type
from app.utils.constants import ALL_PART_TYPES
from app.core.update_manager import UpdateManager

async def main():
    parser = setup_arg_parser()
    args = parser.parse_args()

    targets = validate_args(args)
    if not targets:
        print("Error: Please specify part types or use --all")
        print(f"Supported types: {', '.join(ALL_PART_TYPES)}")
        sys.exit(1)

    if args.update:
        print("Running incremental update mode")
        manager = UpdateManager(
            staging_dir=args.input, 
            output_dir=args.output,
            master_dir=args.master
        )
        await manager.run_update(targets, args)
    else:
        print("Running full mode")
    
        enricher = PartEnricher(
            use_browser=args.use_browser,
            docker_mode=args.docker_mode
        )
        await enricher.launch_browser()
        
        try:
            print(f"Queue: {', '.join(targets)}")
            print(f"Mode: {'BROWSER (slow & safe)' if args.use_browser else 'FAST (risky)'}")
            
            for part in targets:
                await process_part_type(
                    part_type=part, 
                    enricher=enricher, 
                    input_dir=args.input, 
                    output_dir=args.output, 
                    limit=args.limit,
                    start_index=args.start_index
                )
        finally:
            await enricher.stop_browser()

    print("\nAll tasks completed.")

if __name__ == "__main__":
    asyncio.run(main())