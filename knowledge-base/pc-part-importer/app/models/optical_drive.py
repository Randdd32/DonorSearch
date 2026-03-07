from sqlalchemy import ForeignKey, SmallInteger
from sqlalchemy.orm import Mapped, mapped_column
from .component import Component

class OpticalDrive(Component):
    __tablename__ = "component_optical_drive"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)
    
    form_factor_id: Mapped[int | None] = mapped_column(ForeignKey("dic_form_factor_optical_drive.id"))
    interface_id: Mapped[int | None] = mapped_column(ForeignKey("dic_storage_interface.id"))

    __mapper_args__ = {"polymorphic_identity": "OPTICAL_DRIVE"}