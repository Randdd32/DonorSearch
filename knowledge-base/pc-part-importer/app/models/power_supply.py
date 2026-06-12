from sqlalchemy import ForeignKey, SmallInteger
from sqlalchemy.orm import Mapped, mapped_column
from .component import Component

class PowerSupply(Component):
    __tablename__ = "component_power_supply"
    id: Mapped[int] = mapped_column(ForeignKey("component.id", ondelete="CASCADE"), primary_key=True)

    type_id: Mapped[int | None] = mapped_column(ForeignKey("dic_power_supply_type.id"))
    efficiency_id: Mapped[int | None] = mapped_column(ForeignKey("dic_efficiency_rating.id"))
    modular_id: Mapped[int | None] = mapped_column(ForeignKey("dic_modular_type.id"))
    color_id: Mapped[int | None] = mapped_column(ForeignKey("dic_color.id"))

    wattage_w: Mapped[int] = mapped_column(SmallInteger, nullable=False)
    length_mm: Mapped[int | None] = mapped_column(SmallInteger)

    atx_4pin_connectors: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    eps_8pin_connectors: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)

    pcie_12vhpwr_connectors: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    pcie_12pin_connectors: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    pcie_8pin_connectors: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    pcie_6plus2pin_connectors: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    pcie_6pin_connectors: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)

    sata_connectors: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)
    molex_4pin_connectors: Mapped[int] = mapped_column(SmallInteger, nullable=False, default=0)

    __mapper_args__ = {"polymorphic_identity": "POWER_SUPPLY"}