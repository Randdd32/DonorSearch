from sqlalchemy import String, Identity
from sqlalchemy.orm import Mapped, mapped_column
from .base import Base

class Manufacturer(Base):
    __tablename__ = "dic_manufacturer"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(100), unique=True, nullable=False)

class StorageInterface(Base):
    __tablename__ = "dic_storage_interface"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class OpticalDriveFormFactor(Base):
    __tablename__ = "dic_optical_drive_form_factor"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class GpuChipset(Base):
    __tablename__ = "dic_gpu_chipset"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(100), unique=True, nullable=False)

class MemoryType(Base):
    __tablename__ = "dic_memory_type"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class ExpansionInterface(Base):
    __tablename__ = "dic_expansion_interface"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class Color(Base):
    __tablename__ = "dic_color"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(100), unique=True, nullable=False)

class CaseType(Base):
    __tablename__ = "dic_case_type"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class SidePanel(Base):
    __tablename__ = "dic_side_panel"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class MotherboardFormFactor(Base):
    __tablename__ = "dic_motherboard_form_factor"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class FrontPanelUsb(Base):
    __tablename__ = "dic_front_panel_usb"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class CpuSocket(Base):
    __tablename__ = "dic_cpu_socket"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class PowerSupplyType(Base):
    __tablename__ = "dic_power_supply_type"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class EfficiencyRating(Base):
    __tablename__ = "dic_efficiency_rating"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)

class ModularType(Base):
    __tablename__ = "dic_modular_type"
    id: Mapped[int] = mapped_column(Identity(always=True), primary_key=True)
    name: Mapped[str] = mapped_column(String(50), unique=True, nullable=False)