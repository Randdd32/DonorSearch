from sqlalchemy import String, ForeignKey, Identity, Text, Index
from sqlalchemy.orm import Mapped, mapped_column
from .base import Base

class OpticalDrive(Base):
    __tablename__ = "component_optical_drive"

    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    search_name: Mapped[str] = mapped_column(Text, nullable=False)
    
    manufacturer_id: Mapped[int] = mapped_column(ForeignKey("dic_manufacturer.id"))
    form_factor_id: Mapped[int] = mapped_column(ForeignKey("dic_form_factor_optical_drive.id"))
    interface_id: Mapped[int] = mapped_column(ForeignKey("dic_storage_interface.id"))

Index('idx_optical_drive_search_name_trgm', OpticalDrive.search_name, postgresql_using='gin', postgresql_ops={'search_name': 'gin_trgm_ops'})

class OpticalDrivePartNumber(Base):
    __tablename__ = "link_optical_drive_part_number"

    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    optical_drive_id: Mapped[int] = mapped_column(ForeignKey("component_optical_drive.id", ondelete="CASCADE"))
    part_number: Mapped[str] = mapped_column(String(100), nullable=False, index=True)