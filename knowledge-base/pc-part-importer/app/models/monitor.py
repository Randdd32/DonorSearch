from sqlalchemy import ForeignKey, SmallInteger, Float
from sqlalchemy.orm import Mapped, mapped_column
from .base import Base
from .component import Component

class Monitor(Component):
    __tablename__ = "component_monitor"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)

    resolution_id: Mapped[int | None] = mapped_column(ForeignKey("dic_monitor_resolution.id"))
    panel_type_id: Mapped[int | None] = mapped_column(ForeignKey("dic_panel_type.id"))
    aspect_ratio_id: Mapped[int | None] = mapped_column(ForeignKey("dic_aspect_ratio.id"))

    screen_size_in: Mapped[float] = mapped_column(Float, nullable=False)
    refresh_rate_hz: Mapped[int | None] = mapped_column(SmallInteger)
    response_time_ms: Mapped[float | None] = mapped_column(Float)

    input_hdmi: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_dp: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_dvi: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_vga: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_usb_c: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_mini_hdmi: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_micro_hdmi: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_mini_dp: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_bnc: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_component: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_s_video: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    input_virtual_link: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)

    __mapper_args__ = {"polymorphic_identity": "MONITOR"}