from sqlalchemy import ForeignKey, SmallInteger, Boolean, Table, Column, Integer
from sqlalchemy.orm import Mapped, mapped_column, relationship
from .base import Base
from .component import Component

link_storage_interface = Table(
    "link_storage_interface",
    Base.metadata,
    Column("storage_id", ForeignKey("component_storage.id", ondelete="CASCADE"), primary_key=True),
    Column("interface_id", ForeignKey("dic_storage_interface.id", ondelete="CASCADE"), primary_key=True)
)

class Storage(Component):
    __tablename__ = "component_storage"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)

    type_id: Mapped[int | None] = mapped_column(ForeignKey("dic_storage_type.id"))
    form_factor_id: Mapped[int | None] = mapped_column(ForeignKey("dic_storage_form_factor.id"))
    color_id: Mapped[int | None] = mapped_column(ForeignKey("dic_color.id"))

    is_external: Mapped[bool] = mapped_column(Boolean, nullable=False)
    
    capacity_gb: Mapped[int] = mapped_column(Integer, nullable=False) 
    cache_mb: Mapped[int | None] = mapped_column(Integer)
    rpm: Mapped[int | None] = mapped_column(SmallInteger)

    interfaces: Mapped[list["StorageInterface"]] = relationship(secondary=link_storage_interface)

    __mapper_args__ = {"polymorphic_identity": "STORAGE"}