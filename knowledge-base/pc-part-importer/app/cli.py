import argparse
import os
from app.utils.constants import ALL_PART_TYPES

def setup_arg_parser():
    parser = argparse.ArgumentParser(description='Hardware data importer')
    
    parser.add_argument('part_types', nargs='*', 
                        help='List of part types to process (e.g. cpu video-card). Ignored if --all is used.')
    
    parser.add_argument('--all', action='store_true', 
                        help='Process ALL supported part types')
    
    parser.add_argument('--dir', default=os.getenv("DATA_DIR", "./data"), 
                        help='Directory containing the CSV files to import (default: ./data)')

    parser.add_argument('--update', action='store_true',
                        help='Run in incremental update mode (skips DB schema initialization and uses delta logic).')

    return parser

def validate_args(args):
    if args.all:
        return ALL_PART_TYPES
    elif args.part_types:
        valid_types =[pt for pt in args.part_types if pt in ALL_PART_TYPES]
        invalid_types = set(args.part_types) - set(valid_types)
        if invalid_types:
            print(f"Warning: Ignoring unknown part types: {', '.join(invalid_types)}")
        return valid_types
    else:
        print("No specific parts or --all flag provided. Defaulting to processing ALL part types.")
        return ALL_PART_TYPES