import os
import pandas as pd
from app.utils.csv_handler import load_csv, save_csv

async def process_part_type(part_type, enricher, input_dir, output_dir, limit=0):
    input_path = os.path.join(input_dir, f'{part_type}.csv')
    output_path = os.path.join(output_dir, f'{part_type}_enriched.csv')

    try:
        df = load_csv(input_path)
    except FileNotFoundError:
        print(f"Skipping '{part_type}': Input file not found.")
        return False
    except Exception as e:
        print(f"Skipping '{part_type}': Error loading CSV - {e}")
        return False

    enriched_rows = []
    
    target_df = df if limit == 0 else df.head(limit)
    total = len(target_df)

    print(f"--- Processing: {part_type} (Count: {total}) ---")

    for index, row in target_df.iterrows():
        url = row.get('part_url')
        base_row_dict = row.to_dict()

        if pd.isna(url):
            enriched_rows.append(base_row_dict)
            continue
            
        try:
            enriched_data = await enricher.enrich_part(part_type, url, base_row_dict)
            enriched_rows.append(enriched_data)
        except Exception as e:
            print(f"Error processing row {index} in {part_type}: {e}")
            enriched_rows.append(base_row_dict)

    save_csv(enriched_rows, output_path)
    return True