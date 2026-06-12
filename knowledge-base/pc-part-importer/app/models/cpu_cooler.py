from sqlalchemy import ForeignKey, SmallInteger, Boolean, Table, Column
from sqlalchemy.orm import Mapped, mapped_column, relationship
from .base import Base
from .component import Component

link_cpu_cooler_socket = Table(
    "link_cpu_cooler_socket",
    Base.metadata,
    Column("cpu_cooler_id", ForeignKey("component_cpu_cooler.id", ondelete="CASCADE"), primary_key=True),
    Column("socket_id", ForeignKey("dic_cpu_socket.id", ondelete="CASCADE"), primary_key=True)
)

class CpuCooler(Component):
    __tablename__ = "component_cpu_cooler"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)

    color_id: Mapped[int | None] = mapped_column(ForeignKey("dic_color.id"))

    is_water_cooled: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    height_mm: Mapped[int | None] = mapped_column(SmallInteger)
    water_cooled_size_mm: Mapped[int | None] = mapped_column(SmallInteger)

    rpm_min: Mapped[int | None] = mapped_column(SmallInteger)
    rpm_max: Mapped[int | None] = mapped_column(SmallInteger)

    sockets: Mapped[list["CpuSocket"]] = relationship(secondary=link_cpu_cooler_socket)

    __mapper_args__ = {"polymorphic_identity": "CPU_COOLER"}