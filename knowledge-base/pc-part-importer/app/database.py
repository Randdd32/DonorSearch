import os
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker
from app.models.base import Base

DB_URL = os.getenv("DB_URL", "postgresql://postgres:postgres@localhost:5432/donor_search_db")

engine = create_engine(DB_URL, echo=False)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def init_db():
    with engine.begin() as conn:
        conn.execute(text("CREATE EXTENSION IF NOT EXISTS pg_trgm;"))
    
    Base.metadata.create_all(bind=engine)