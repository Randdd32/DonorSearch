from sqlalchemy import String, ForeignKey, Identity, Text, Index
from sqlalchemy.orm import Mapped, mapped_column
from .base import Base

class Component(Base):
    __tablename__ = "component"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    type: Mapped[str] = mapped_column(String(50), nullable=False)
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    search_name: Mapped[str] = mapped_column(Text, nullable=False)
    manufacturer_id: Mapped[int | None] = mapped_column(ForeignKey("dic_manufacturer.id"))

    __mapper_args__ = {
        "polymorphic_on": "type",
        "polymorphic_identity": "COMPONENT"
    }

class PartNumber(Base):
    __tablename__ = "link_part_number"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    component_id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), nullable=False)
    part_number: Mapped[str] = mapped_column(String(100), nullable=False, index=True)

Index('idx_component_search_name_trgm', Component.search_name, postgresql_using='gin', postgresql_ops={'search_name': 'gin_trgm_ops'})