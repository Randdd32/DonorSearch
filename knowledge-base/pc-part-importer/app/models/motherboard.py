from sqlalchemy import ForeignKey, SmallInteger, Boolean
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import Mapped, mapped_column
from .component import Component

class Motherboard(Component):
    __tablename__ = "component_motherboard"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)

    socket_id: Mapped[int | None] = mapped_column(ForeignKey("dic_cpu_socket.id"))
    form_factor_id: Mapped[int | None] = mapped_column(ForeignKey("dic_motherboard_form_factor.id"))
    memory_type_id: Mapped[int | None] = mapped_column(ForeignKey("dic_memory_type.id"))
    color_id: Mapped[int | None] = mapped_column(ForeignKey("dic_color.id"))

    max_memory_gb: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    memory_slots: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    memory_speed_max_mhz: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    
    ecc_support: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    uses_back_connect: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)

    m2_slots: Mapped[list] = mapped_column(JSONB, nullable=False, server_default='[]')

    sata_6_ports: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    sata_3_ports: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    
    pci_x16_slots: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    pci_x8_slots: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    pci_x4_slots: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    pci_x1_slots: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    pci_slots: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    mini_pcie_msata_slots: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)

    header_usb_2_0: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    header_usb_3_2_gen_1: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    header_usb_3_2_gen_2: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    header_usb_3_2_gen_2x2: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    header_usb_2_0_single_port: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)

    __mapper_args__ = {"polymorphic_identity": "MOTHERBOARD"}