from sqlalchemy import ForeignKey, SmallInteger, String, Float
from sqlalchemy.orm import Mapped, mapped_column
from .base import Base
from .component import Component

class ExpansionCard(Component):
    __tablename__ = "component_expansion_card"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)
    
    card_type: Mapped[str] = mapped_column(String(50), nullable=False)

    interface_id: Mapped[int | None] = mapped_column(ForeignKey("dic_expansion_interface.id"))
    color_id: Mapped[int | None] = mapped_column(ForeignKey("dic_color.id"))

    audio_chipset_id: Mapped[int | None] = mapped_column(ForeignKey("dic_audio_chipset.id"))
    channels: Mapped[float | None] = mapped_column(Float)
    digital_audio_bit: Mapped[int | None] = mapped_column(SmallInteger)
    sample_rate_khz: Mapped[float | None] = mapped_column(Float)

    protocol_id: Mapped[int | None] = mapped_column(ForeignKey("dic_wireless_protocol.id"))

    __mapper_args__ = {"polymorphic_identity": "EXPANSION_CARD"}