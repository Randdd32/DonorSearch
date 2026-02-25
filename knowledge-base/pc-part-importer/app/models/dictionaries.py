from sqlalchemy import String, Identity
from sqlalchemy.orm import Mapped, mapped_column
from .base import Base

class Manufacturer(Base):
    __tablename__ = "dic_manufacturer"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(100), unique=True, nullable=False)

class StorageInterface(Base):
    __tablename__ = "dic_storage_interface"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class OpticalDriveFormFactor(Base):
    __tablename__ = "dic_form_factor_optical_drive"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)