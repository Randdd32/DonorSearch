from sqlalchemy import ForeignKey, SmallInteger
from sqlalchemy.orm import Mapped, mapped_column
from .component import Component

class OpticalDrive(Component):
    __tablename__ = "component_optical_drive"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)
    
    form_factor_id: Mapped[int | None] = mapped_column(ForeignKey("dic_optical_drive_form_factor.id"))
    interface_id: Mapped[int | None] = mapped_column(ForeignKey("dic_storage_interface.id"))

    bd_speed_x: Mapped[int | None] = mapped_column(SmallInteger)
    dvd_speed_x: Mapped[int | None] = mapped_column(SmallInteger)
    cd_speed_x: Mapped[int | None] = mapped_column(SmallInteger)

    __mapper_args__ = {"polymorphic_identity": "OPTICAL_DRIVE"}