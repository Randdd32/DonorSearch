import argparse
import asyncio
import os
import pandas as pd
from app.enricher import PartEnricher
from app.csv_handler import load_csv, save_csv

async def main():
    parser = argparse.ArgumentParser(description='Enrich PC parts data')
    parser.add_argument('part_type', help='Type of part (e.g. video-card, cpu)')
    parser.add_argument('--input', default='/data/input', help='Input CSV directory')
    parser.add_argument('--output', default='/data/output', help='Output CSV directory')
    parser.add_argument('--limit', type=int, default=0, help='Limit number of items (0=all)')
    
    args = parser.parse_args()
    
    input_path = os.path.join(args.input, f"{args.part_type}.csv")
    output_path = os.path.join(args.output, f"{args.part_type}_enriched.csv")

    try:
        df = load_csv(input_path)
    except Exception as e:
        print(f"Error loading input: {e}")
        return

    enricher = PartEnricher()
    enriched_rows = []
    
    target_df = df if args.limit == 0 else df.head(args.limit)
    total = len(target_df)

    print(f"Starting enrichment for {args.part_type}. Count: {total}")

    for index, row in target_df.iterrows():
        url = row['part_url']
        base_row_dict = row.to_dict()

        if pd.isna(url):
            enriched_rows.append(base_row_dict)
            continue
            
        enrich_type = args.part_type
        if args.part_type == 'storage':
            enrich_type = 'internal-hard-drive'
        elif args.part_type == 'expansion-card':
            enrich_type = 'wired-network-card'
            
        enriched_data = await enricher.enrich_part(enrich_type, url, base_row_dict)
        enriched_rows.append(enriched_data)

    save_csv(enriched_rows, output_path)

if __name__ == "__main__":
    asyncio.run(main())