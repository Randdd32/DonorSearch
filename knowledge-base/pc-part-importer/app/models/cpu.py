from sqlalchemy import ForeignKey, SmallInteger, Boolean, Float
from sqlalchemy.orm import Mapped, mapped_column
from .base import Base
from .component import Component

class Cpu(Component):
    __tablename__ = "component_cpu"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)

    socket_id: Mapped[int | None] = mapped_column(ForeignKey("dic_cpu_socket.id"))
    microarchitecture_id: Mapped[int | None] = mapped_column(ForeignKey("dic_microarchitecture.id"))
    graphics_id: Mapped[int | None] = mapped_column(ForeignKey("dic_integrated_graphics.id"))

    core_count: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    core_clock_ghz: Mapped[float] = mapped_column(Float, nullable=False)
    boost_clock_ghz: Mapped[float | None] = mapped_column(Float)
    tdp_w: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    max_memory_gb: Mapped[int | None] = mapped_column(SmallInteger)
    ecc_support: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)

    __mapper_args__ = {"polymorphic_identity": "CPU"}