from sqlalchemy.orm import Session
from app.models.base import Base

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