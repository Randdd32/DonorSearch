from sqlalchemy import ForeignKey, SmallInteger, String, Table, Column, Float
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import Mapped, mapped_column, relationship
from .base import Base
from .component import Component

link_case_mobo_form_factor = Table(
    "link_case_mobo_form_factor",
    Base.metadata,
    Column("case_id", ForeignKey("component_case.id", ondelete="CASCADE"), primary_key=True),
    Column("form_factor_id", ForeignKey("dic_motherboard_form_factor.id", ondelete="CASCADE"), primary_key=True)
)

link_case_front_panel_usb = Table(
    "link_case_front_panel_usb",
    Base.metadata,
    Column("case_id", ForeignKey("component_case.id", ondelete="CASCADE"), primary_key=True),
    Column("usb_id", ForeignKey("dic_front_panel_usb.id", ondelete="CASCADE"), primary_key=True)
)

class Case(Component):
    __tablename__ = "component_case"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)

    type_id: Mapped[int | None] = mapped_column(ForeignKey("dic_case_type.id"))
    color_id: Mapped[int | None] = mapped_column(ForeignKey("dic_color.id"))
    side_panel_id: Mapped[int | None] = mapped_column(ForeignKey("dic_side_panel.id"))

    length_mm: Mapped[int | None] = mapped_column(SmallInteger)
    width_mm: Mapped[int | None] = mapped_column(SmallInteger)
    height_mm: Mapped[int | None] = mapped_column(SmallInteger)
    max_gpu_len_mm: Mapped[int | None] = mapped_column(SmallInteger)
    max_cpu_cooler_height_mm: Mapped[int | None] = mapped_column(SmallInteger)
    psu_w: Mapped[int | None] = mapped_column(SmallInteger)

    int_35_bays: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    ext_525_bays: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    ext_35_bays: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    int_25_bays: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)

    expansion_slots_full_height: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    expansion_slots_half_height: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    expansion_slots_riser: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)

    radiator_support_text: Mapped[str | None] = mapped_column(String(500))
    fan_support_text: Mapped[str | None] = mapped_column(String(500))

    radiator_sizes: Mapped[list] = mapped_column(JSONB, nullable=False, server_default='[]')
    fan_sizes: Mapped[list] = mapped_column(JSONB, nullable=False, server_default='[]')

    mobo_form_factors: Mapped[list["MotherboardFormFactor"]] = relationship(secondary=link_case_mobo_form_factor)
    front_panel_usbs: Mapped[list["FrontPanelUsb"]] = relationship(secondary=link_case_front_panel_usb)

    __mapper_args__ = {"polymorphic_identity": "CASE"}