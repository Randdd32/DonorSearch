from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker
from app.models.base import Base
from app.utils.constants import DB_URL

import app.models.dictionaries
import app.models.component
import app.models.optical_drive
import app.models.video_card
import app.models.case
import app.models.power_supply
import app.models.case_fan
import app.models.cpu
import app.models.cpu_cooler
import app.models.storage
import app.models.expansion_card

engine = create_engine(DB_URL, echo=False)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def init_db():
    with engine.begin() as conn:
        conn.execute(text("CREATE EXTENSION IF NOT EXISTS pg_trgm;"))
    
    Base.metadata.create_all(bind=engine)