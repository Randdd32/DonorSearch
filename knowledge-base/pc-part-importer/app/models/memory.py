from sqlalchemy import ForeignKey, SmallInteger, Boolean
from sqlalchemy.orm import Mapped, mapped_column
from .base import Base
from .component import Component

class Memory(Component):
    __tablename__ = "component_memory"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)

    form_factor_id: Mapped[int | None] = mapped_column(ForeignKey("dic_ram_form_factor.id"))
    memory_type_id: Mapped[int | None] = mapped_column(ForeignKey("dic_memory_type.id"))
    color_id: Mapped[int | None] = mapped_column(ForeignKey("dic_color.id"))

    cas_latency: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    frequency_mhz: Mapped[int | None] = mapped_column(SmallInteger)
    modules_count: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    modules_size_gb: Mapped[int] = mapped_column(SmallInteger, nullable=False)

    is_ecc: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    is_registered: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)

    __mapper_args__ = {"polymorphic_identity": "MEMORY"}