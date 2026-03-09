from sqlalchemy import ForeignKey, SmallInteger, Boolean, Table, Column
from sqlalchemy.orm import Mapped, mapped_column, relationship
from .base import Base
from .component import Component

link_case_fan_connector = Table(
    "link_case_fan_connector",
    Base.metadata,
    Column("case_fan_id", ForeignKey("component_case_fan.id", ondelete="CASCADE"), primary_key=True),
    Column("connector_id", ForeignKey("dic_fan_connector.id", ondelete="CASCADE"), primary_key=True)
)

class CaseFan(Component):
    __tablename__ = "component_case_fan"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)

    color_id: Mapped[int | None] = mapped_column(ForeignKey("dic_color.id"))
    
    size_mm: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    pwm: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)

    rpm_min: Mapped[int | None] = mapped_column(SmallInteger)
    rpm_max: Mapped[int | None] = mapped_column(SmallInteger)
    airflow_min: Mapped[int | None] = mapped_column(SmallInteger)
    airflow_max: Mapped[int | None] = mapped_column(SmallInteger)

    connectors: Mapped[list["FanConnector"]] = relationship(secondary=link_case_fan_connector)

    __mapper_args__ = {"polymorphic_identity": "CASE_FAN"}