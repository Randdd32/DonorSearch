import argparse
from app.utils.constants import ALL_PART_TYPES

def setup_arg_parser():
    parser = argparse.ArgumentParser(description='Enrich PC parts data from PCPartPicker')
    
    parser.add_argument('part_types', nargs='*', 
                        help='List of part types to process (e.g. cpu video-card). Ignored if --all is used.')
    
    parser.add_argument('--all', action='store_true', 
                        help='Process ALL supported part types')
    
    parser.add_argument('--input', default='/data/input', help='Input CSV directory')
    parser.add_argument('--output', default='/data/output', help='Output CSV directory')
    parser.add_argument('--limit', type=int, default=0, help='Limit items per category (0=all, for testing)')
    
    parser.add_argument('--use-browser', action='store_true', 
                        help='Use real browser (Camoufox) instead of lightweight requests. Slower but bypasses bans.')

    parser.add_argument('--docker-mode', action='store_true',
                        help='Use "virtual" headless mode (requires Xvfb/Linux). Use this inside Docker.')

    parser.add_argument('--update', action='store_true',
                        help='Run in incremental update mode (scrape only new items).')
    parser.add_argument('--master', default='/data/master', help='Master CSV directory')

    parser.add_argument('--start-index', type=int, default=0, 
                        help='Row index to start processing from (useful for resuming interrupted full scrapes).')

    return parser

def validate_args(args):
    if args.all:
        return ALL_PART_TYPES
    elif args.part_types:
        return args.part_types
    else:
        return None