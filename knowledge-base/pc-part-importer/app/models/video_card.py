from sqlalchemy import ForeignKey, SmallInteger
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import Mapped, mapped_column
from .component import Component

class VideoCard(Component):
    __tablename__ = "component_video_card"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)

    chipset_id: Mapped[int | None] = mapped_column(ForeignKey("dic_gpu_chipset.id"))
    memory_type_id: Mapped[int | None] = mapped_column(ForeignKey("dic_memory_type.id"))
    interface_id: Mapped[int | None] = mapped_column(ForeignKey("dic_expansion_interface.id"))
    color_id: Mapped[int | None] = mapped_column(ForeignKey("dic_color.id"))

    memory_gb: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    core_clock_mhz: Mapped[int | None] = mapped_column(SmallInteger)
    boost_clock_mhz: Mapped[int | None] = mapped_column(SmallInteger)
    
    length_mm: Mapped[int | None] = mapped_column(SmallInteger)
    tdp_w: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    slot_width: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    case_expansion_width: Mapped[int] = mapped_column(SmallInteger, nullable=False)

    power_6pin_count: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    power_8pin_count: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    power_12pin_count: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    power_12vhpwr_count: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    power_eps_count: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)

    video_outputs: Mapped[dict] = mapped_column(JSONB, nullable=False)

    __mapper_args__ = {"polymorphic_identity": "VIDEO_CARD"}