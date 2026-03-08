from sqlalchemy.orm import Session
from app.models.base import Base
from app.models.dictionaries import Manufacturer
from app.models.component import PartNumber
from app.utils.parsing_utils import parse_separated_string, clean_search_tokens

class BaseImporter:
    def __init__(self, session: Session):
        self.session = session
        self._cache = {}

    def get_or_create_dictionary(self, model: type[Base], name: str):
        if not name or str(name).lower() == 'nan':
            return None
            
        cache_key = f"{model.__name__}_{name}"
        if cache_key in self._cache:
            return self._cache[cache_key]

        instance = self.session.query(model).filter_by(name=name).first()
        if not instance:
            instance = model(name=name)
            self.session.add(instance)
            self.session.flush()

        self._cache[cache_key] = instance.id
        return instance.id

    def get_m2m_entities(self, model: type[Base], raw_string: str) -> list[Base]:
        values = parse_separated_string(raw_string)
        entities =[]
        for val in values:
            dict_id = self.get_or_create_dictionary(model, val)
            if dict_id:
                entity = self.session.get(model, dict_id)
                if entity:
                    entities.append(entity)
        return entities

    def build_base_component(self, row, additional_search_tokens=None):
        manufacturer_id = self.get_or_create_dictionary(Manufacturer, row.get('manufacturer'))
        part_numbers_list = parse_separated_string(row.get('part_number'))
        
        search_tokens =[
            str(row.get('manufacturer', '')),
            str(row.get('name', ''))
        ]
        if additional_search_tokens:
            search_tokens.extend(additional_search_tokens)
        search_tokens.append(" ".join(part_numbers_list))
        
        search_name = clean_search_tokens(search_tokens)

        return {
            "name": row.get('name'),
            "search_name": search_name,
            "manufacturer_id": manufacturer_id
        }, part_numbers_list

    def save_part_numbers(self, component_id: int, part_numbers_list: list[str]):
        for pn in part_numbers_list:
            if pn.strip():
                pn_record = PartNumber(component_id=component_id, part_number=pn.strip())
                self.session.add(pn_record)