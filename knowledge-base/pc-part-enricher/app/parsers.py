import re

def clean_unit(value, unit_str=''):
    """Убирает единицы измерения и возвращает число (int или float)."""
    if not value or not isinstance(value, str):
        return None
    
    clean = re.sub(rf'\s*{unit_str}$', '', value, flags=re.IGNORECASE).strip()
   
    if ' - ' in clean:
        parts = clean.split(' - ')
        clean = parts[-1]
    
    try:
        if '.' in clean:
            return float(clean)
        return int(clean)
    except ValueError:
        return None

def clean_bool(value):
    """Превращает 'Yes'/'No' в True/False."""
    if not value:
        return None
    if isinstance(value, list):
        value = value[0]
    return True if str(value).lower() == 'yes' else False

def list_to_str(value_list, separator='|'):
    """Превращает список JSON в строку."""
    if not value_list:
        return None
    return separator.join(value_list)

def get_first_val(specs, key):
    """Безопасно берет первый элемент списка по ключу."""
    val = specs.get(key)
    if val and isinstance(val, list) and len(val) > 0:
        return val[0]
    return None

def extract_count_from_list(value_list, keyword):
    """
    Ищет строку в списке, содержащую keyword (например 'External 5.25').
    Извлекает число перед 'x' (например '2 x ...').
    """
    if not value_list:
        return 0
    
    for item in value_list:
        if keyword.lower() in item.lower():
            match = re.search(r'^(\d+)', item)
            if match:
                return int(match.group(1))
    return 0

def parse_dynamic_counts(specs, suffix_key, exclude_keys=None):
    """
    Ищет ключи, заканчивающиеся на suffix_key (например 'Connectors').
    Возвращает словарь {snake_case_key: count}.
    """
    result = {}
    if exclude_keys is None:
        exclude_keys = []

    for key, value in specs.items():
        if key in exclude_keys:
            continue
            
        if key.endswith(suffix_key):
            val_str = value[0] if isinstance(value, list) and value else str(value)
            match = re.search(r'^(\d+)', val_str)
            count = int(match.group(1)) if match else 0
            
            clean_key = key.lower().replace(suffix_key.lower(), '').strip()
            clean_key = re.sub(r'[^a-z0-9]', '_', clean_key)
            clean_key = re.sub(r'_+', '_', clean_key).strip('_')
            
            col_name = f'{clean_key}_{suffix_key.lower().replace(' ', '_')}'
            result[col_name] = count
    return result

def get_base_info(specs):
    """
    Извлекает общие поля для любой детали.
    """
    return {
        'manufacturer': get_first_val(specs, 'Manufacturer'),
        'part_number': list_to_str(specs.get('Part #'))
    }

def parse_case(specs):
    data = get_base_info(specs) 
    
    drive_bays = specs.get('Drive Bays', [])
    data['ext_525_bays'] = extract_count_from_list(drive_bays, 'External 5.25')
    data['ext_35_bays'] = extract_count_from_list(drive_bays, 'External 3.5')
    data['int_25_bays'] = extract_count_from_list(drive_bays, 'Internal 2.5')
    
    data['expansion_slots'] = clean_unit(get_first_val(specs, 'Expansion Slots'))
    
    gpu_len_raw = specs.get('Maximum Video Card Length')
    if gpu_len_raw:
        lengths = [clean_unit(x, 'mm') for x in gpu_len_raw]
        lengths = [x for x in lengths if x is not None]
        data['max_gpu_len'] = max(lengths) if lengths else None
    
    data['max_cpu_cooler_height'] = clean_unit(get_first_val(specs, 'Maximum CPU Cooler Height'), 'mm')
    
    data['mobo_form_factor'] = list_to_str(specs.get('Motherboard Form Factor', []))
    data['front_panel_usb'] = list_to_str(specs.get('Front Panel USB', []))
    data['radiator_support'] = list_to_str(specs.get('Radiator Support', []))
    data['fan_support'] = list_to_str(specs.get('Fan Support', []))

    return data

def parse_cpu(specs):
    data = get_base_info(specs) 

    data['socket'] = get_first_val(specs, 'Socket')
    data['max_memory'] = clean_unit(get_first_val(specs, 'Maximum Supported Memory'), 'GB')
    data['ecc_support'] = clean_bool(get_first_val(specs, 'ECC Support'))

    return data

def parse_cpu_cooler(specs):
    data = get_base_info(specs)
    
    data['height'] = clean_unit(get_first_val(specs, 'Height'), 'mm')
    data['supported_sockets'] = list_to_str(specs.get('CPU Socket', []))
        
    return data

def parse_motherboard(specs):
    data = get_base_info(specs) 
    
    data['memory_type'] = get_first_val(specs, 'Memory Type')
    
    mem_speed_raw = specs.get('Memory Speed', [])
    speeds = [clean_unit(x) for x in mem_speed_raw]
    speeds = [x for x in speeds if x is not None]
    data['memory_speed_max'] = max(speeds) if speeds else None
    
    data['m2_slots'] = len(specs.get('M.2 Slots', []))
    data['sata_6_ports'] = clean_unit(get_first_val(specs, 'SATA 6.0 Gb/s Ports'))
    data['sata_3_ports'] = clean_unit(get_first_val(specs, 'SATA 3.0 Gb/s Ports'))
    
    data['pci_x16_slots'] = clean_unit(get_first_val(specs, 'PCIe x16 Slots'))
    data['pci_x8_slots'] = clean_unit(get_first_val(specs, 'PCIe x8 Slots'))
    data['pci_x4_slots'] = clean_unit(get_first_val(specs, 'PCIe x4 Slots'))
    data['pci_x1_slots'] = clean_unit(get_first_val(specs, 'PCIe x1 Slots'))
    data['pci_slots'] = clean_unit(get_first_val(specs, 'PCI Slots'))
    data['mini_pcie_msata_slots'] = clean_unit(get_first_val(specs, 'Mini-PCIe / mSATA Slots'))
    
    data['uses_back_connect'] = clean_bool(get_first_val(specs, 'Uses Back-Connect Connectors'))
    data['ecc_support'] = clean_bool(get_first_val(specs, 'Supports ECC'))
    
    usb_headers = {}
    for key, value in specs.items():
        if 'USB' in key and 'Headers' in key:
            val_str = value[0] if value else '0'
            match = re.search(r'^(\d+)', str(val_str))
            count = int(match.group(1)) if match else 0
            
            clean_key = key.replace('Headers', '').strip().replace(' ', '_').replace('.', '_').lower()
            usb_headers[f'header_{clean_key}'] = count
    data.update(usb_headers)
    
    return data

def parse_memory(specs):
    data = get_base_info(specs) 

    data['form_factor'] = get_first_val(specs, 'Form Factor')
    
    ecc_reg_str = get_first_val(specs, 'ECC / Registered')
    if ecc_reg_str:
        data['is_ecc'] = 'ECC' in ecc_reg_str and 'Non-ECC' not in ecc_reg_str
        data['is_registered'] = 'Registered' in ecc_reg_str
    else:
        data['is_ecc'] = None
        data['is_registered'] = None
        
    return data

def parse_storage(specs):
    data = get_base_info(specs) 
    
    raw_rpm = get_first_val(specs, 'RPM')
    if raw_rpm == 'SSD':
        data['type'] = 'SSD'
        data['rpm'] = None
    elif raw_rpm:
        data['type'] = 'HDD'
        data['rpm'] = clean_unit(raw_rpm, 'RPM')
    
    data['form_factor'] = get_first_val(specs, 'Form Factor')
    data['interface'] = get_first_val(specs, 'Interface')
    
    return data

def parse_video_card(specs):
    data = get_base_info(specs) 
    
    data['length'] = clean_unit(get_first_val(specs, 'Length'), 'mm')
    data['tdp'] = clean_unit(get_first_val(specs, 'TDP'), 'W')
    data['slot_width'] = clean_unit(get_first_val(specs, 'Total Slot Width'))
    data['case_expansion_width'] = clean_unit(get_first_val(specs, 'Case Expansion Slot Width'))
    data['interface'] = get_first_val(specs, 'Interface')
    data['memory_type'] = get_first_val(specs, 'Memory Type')
    
    power_str = get_first_val(specs, 'External Power')
    data['power_6pin_count'] = 0
    data['power_8pin_count'] = 0
    data['power_12vhpwr_count'] = 0
    data['power_eps_count'] = 0
    
    if power_str:
        if match := re.search(r'(\d+)\s*x\s*PCIe\s*6-pin', power_str):
            data['power_6pin_count'] = int(match.group(1))
        if match := re.search(r'(\d+)\s*x\s*PCIe\s*8-pin', power_str):
            data['power_8pin_count'] = int(match.group(1))
        if match := re.search(r'(\d+)\s*x\s*(?:PCIe\s*16-pin|12VHPWR|PCIe\s*12-pin)', power_str):
            data['power_12vhpwr_count'] = int(match.group(1))
        if match := re.search(r'(\d+)\s*x\s*EPS\s*8-pin', power_str):
            data['power_eps_count'] = int(match.group(1))
            
    outputs = parse_dynamic_counts(specs, 'Outputs')
    data.update(outputs)
    
    return data

def parse_power_supply(specs):
    data = get_base_info(specs)

    data['length'] = clean_unit(get_first_val(specs, 'Length'), 'mm')
    
    connectors = parse_dynamic_counts(specs, 'Connectors')
    data.update(connectors)
    
    return data

def parse_case_fan(specs):
    data = get_base_info(specs)

    data['size'] = clean_unit(get_first_val(specs, 'Size'), 'mm')
    data['connectors'] = get_first_val(specs, 'Connector')

    return data

def parse_monitor(specs):
    data = get_base_info(specs)
    
    inputs_list = specs.get('Inputs', [])
    
    def count_inputs(keyword, exact=False):
        total = 0
        for item in inputs_list:
            if keyword in item:
                if exact:
                    if re.search(rf'(Mini-|Micro-){keyword}', item):
                        continue
                
                match = re.search(r'^(\d+)', item)
                if match:
                    total += int(match.group(1))
        return total

    data['input_hdmi'] = count_inputs('HDMI', exact=True)
    data['input_mini_hdmi'] = count_inputs('Mini-HDMI')
    data['input_micro_hdmi'] = count_inputs('Micro-HDMI')
    
    data['input_dp'] = count_inputs('DisplayPort', exact=True)
    data['input_mini_dp'] = count_inputs('Mini-DisplayPort')
    
    data['input_dvi'] = count_inputs('DVI')
    data['input_vga'] = count_inputs('VGA') + count_inputs('D-Sub')
    data['input_usb_c'] = count_inputs('USB Type-C')
    data['input_thunderbolt'] = count_inputs('Thunderbolt')
    
    return data

def parse_expansion_card(specs):
    data = get_base_info(specs)

    data['interface'] = get_first_val(specs, 'Interface')

    return data

def parse_optical_drive(specs):
    data = get_base_info(specs)

    data['form_factor'] = get_first_val(specs, 'Form Factor')
    data['interface'] = get_first_val(specs, 'Interface')
    
    return data