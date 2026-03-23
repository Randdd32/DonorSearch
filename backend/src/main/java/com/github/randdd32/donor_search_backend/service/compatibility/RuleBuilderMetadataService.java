package com.github.randdd32.donor_search_backend.service.compatibility;

import com.github.randdd32.donor_search_backend.web.dto.compatibility.FieldMetadataDto;
import com.github.randdd32.donor_search_backend.web.dto.compatibility.MethodMetadataDto;
import com.github.randdd32.donor_search_backend.web.dto.compatibility.RuleBuilderMetadataDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RuleBuilderMetadataService {
    public RuleBuilderMetadataDto getMetadata() {
        return new RuleBuilderMetadataDto(
                getContextProperties(),
                getContextMethods(),
                getComponentFields()
        );
    }

    private Map<String, String> getContextProperties() {
        return Map.ofEntries(
                Map.entry("pcCase", "Корпус (объект)"),
                Map.entry("motherboard", "Материнская плата (объект)"),
                Map.entry("psus", "Блоки питания (список)"),
                Map.entry("cpus", "Процессоры (список)"),
                Map.entry("coolers", "Кулеры для процессоров (список)"),
                Map.entry("gpus", "Видеокарты (список)"),
                Map.entry("memories", "Оперативная память (список)"),
                Map.entry("storages", "Накопители (список)"),
                Map.entry("expansionCards", "Карты расширения (список)"),
                Map.entry("caseFans", "Корпусные вентиляторы (список)"),
                Map.entry("opticalDrives", "Оптические приводы (список)"),
                Map.entry("monitors", "Мониторы (список)")
        );
    }

    private List<MethodMetadataDto> getContextMethods() {
        return List.of(
                new MethodMetadataDto("getTotalTdpW()", "Integer", "Общее тепловыделение / TDP (Вт)", true),
                new MethodMetadataDto("getTotalPsuWattage()", "Integer", "Общая мощность блоков питания (Вт)", true),
                new MethodMetadataDto("getTotalRamCapacityGb()", "Integer", "Общий объем ОЗУ (ГБ)", true),
                new MethodMetadataDto("getTotalRamModules()", "Integer", "Общее количество плашек ОЗУ (шт)", true),
                new MethodMetadataDto("getStorageCountByFormFactor('String ffName')", "Integer", "Количество накопителей по форм-фактору (шт)", true),
                new MethodMetadataDto("getSataDevicesCount()", "Integer", "Общее количество SATA-устройств (шт)", true),
                new MethodMetadataDto("getTotalGpuSlotWidth()", "Integer", "Общая толщина видеокарт (в слотах)", true),
                new MethodMetadataDto("isEccSupported()", "Boolean", "Поддерживается ли ECC-память (материнская плата + процессор)", true),
                new MethodMetadataDto("getReqPcie8Pin()", "Integer", "Требуется 8-pin PCIe коннекторов (шт)", true),
                new MethodMetadataDto("getAvailPcie8Pin()", "Integer", "Доступно 8-pin PCIe коннекторов от БП (шт)", true),
                new MethodMetadataDto("getReqPcie6Pin()", "Integer", "Требуется 6-pin PCIe коннекторов (шт)", true),
                new MethodMetadataDto("getAvailPcie6Pin()", "Integer", "Доступно 6-pin PCIe коннекторов от БП (шт)", true),
                new MethodMetadataDto("getReqPcie12vhpwr()", "Integer", "Требуется 12VHPWR коннекторов (шт)", true),
                new MethodMetadataDto("getAvailPcie12vhpwr()", "Integer", "Доступно 12VHPWR коннекторов от БП (шт)", true)
        );
    }

    private Map<String, List<FieldMetadataDto>> getComponentFields() {
        return Map.ofEntries(
                Map.entry("CPU", List.of(
                        new FieldMetadataDto("socket.id", "Long", "ID cокета", true),
                        new FieldMetadataDto("microarchitecture.id", "Long", "ID микроархитектуры", true),
                        new FieldMetadataDto("graphics.id", "Long", "ID встроенной графики", true),
                        new FieldMetadataDto("coreCount", "Integer", "Количество ядер", false),
                        new FieldMetadataDto("coreClockGhz", "Double", "Базовая частота (ГГц)", false),
                        new FieldMetadataDto("tdpW", "Integer", "Тепловыделение (Вт)", false),
                        new FieldMetadataDto("maxMemoryGb", "Integer", "Макс. поддерживаемая память (ГБ)", true),
                        new FieldMetadataDto("eccSupport", "Boolean", "Поддержка ECC", false)
                )),
                Map.entry("MOTHERBOARD", List.of(
                        new FieldMetadataDto("socket.id", "Long", "ID сокета", true),
                        new FieldMetadataDto("formFactor.id", "Long", "ID форм-фактора", true),
                        new FieldMetadataDto("memoryType.id", "Long", "ID поколения памяти (DDR)", true),
                        new FieldMetadataDto("maxMemoryGb", "Integer", "Макс. объем ОЗУ (ГБ)", false),
                        new FieldMetadataDto("memorySlots", "Integer", "Количество слотов ОЗУ", false),
                        new FieldMetadataDto("memorySpeedMaxMhz", "Integer", "Макс. частота памяти (МГц)", false),
                        new FieldMetadataDto("eccSupport", "Boolean", "Поддержка ECC", false),
                        new FieldMetadataDto("usesBackConnect", "Boolean", "Разъемы на обратной стороне", false),
                        new FieldMetadataDto("m2Slots", "List<Object>", "Слоты M.2 (Пример: [{'sizes':[2280], 'keys':'M-key'}]. В SpEL: m2Slots.?[sizes.contains(2280) && keys.contains('M-key')].size() > 0)", false),
                        new FieldMetadataDto("sata3Ports", "Integer", "Порты SATA 3Gb/s", false),
                        new FieldMetadataDto("sata6Ports", "Integer", "Порты SATA 6Gb/s", false),
                        new FieldMetadataDto("pciX16Slots", "Integer", "Слоты PCIe x16", false),
                        new FieldMetadataDto("pciX8Slots", "Integer", "Слоты PCIe x8", false),
                        new FieldMetadataDto("pciX4Slots", "Integer", "Слоты PCIe x4", false),
                        new FieldMetadataDto("pciX1Slots", "Integer", "Слоты PCIe x1", false),
                        new FieldMetadataDto("pciSlots", "Integer", "Слоты PCI", false),
                        new FieldMetadataDto("miniPcieMsataSlots", "Integer", "Слоты Mini-PCIe / mSATA", false),
                        new FieldMetadataDto("headerUsb20", "Integer", "Колодки USB 2.0", false),
                        new FieldMetadataDto("headerUsb32Gen1", "Integer", "Колодки USB 3.2 Gen 1", false),
                        new FieldMetadataDto("headerUsb32Gen2", "Integer", "Колодки USB 3.2 Gen 2", false),
                        new FieldMetadataDto("headerUsb32Gen2x2", "Integer", "Колодки USB 3.2 Gen 2x2", false),
                        new FieldMetadataDto("headerUsb20SinglePort", "Integer", "Колодки USB 2.0 (Single Port)", false)
                )),
                Map.entry("VIDEO_CARD", List.of(
                        new FieldMetadataDto("chipset.id", "Long", "ID графического чипа", true),
                        new FieldMetadataDto("memoryType.id", "Long", "ID типа памяти", true),
                        new FieldMetadataDto("interfaceType.id", "Long", "ID интерфейса", true),
                        new FieldMetadataDto("lengthMm", "Integer", "Длина (мм)", true),
                        new FieldMetadataDto("tdpW", "Integer", "Энергопотребление / TDP (Вт)", false),
                        new FieldMetadataDto("slotWidth", "Integer", "Толщина (в слотах)", false),
                        new FieldMetadataDto("caseExpansionWidth", "Integer", "Ширина планки крепления", false),
                        new FieldMetadataDto("memoryGb", "Integer", "Объем памяти (ГБ)", false),
                        new FieldMetadataDto("coreClockMhz", "Integer", "Базовая частота (МГц)", true),
                        new FieldMetadataDto("power6pinCount", "Integer", "Требуется 6-pin PCIe", false),
                        new FieldMetadataDto("power8pinCount", "Integer", "Требуется 8-pin PCIe", false),
                        new FieldMetadataDto("power12pinCount", "Integer", "Требуется 12-pin PCIe", false),
                        new FieldMetadataDto("power12vhpwrCount", "Integer", "Требуется 12VHPWR", false),
                        new FieldMetadataDto("powerEpsCount", "Integer", "Требуется EPS 8-pin", false),
                        new FieldMetadataDto("videoOutputs", "Map<String, Integer>", "Видеовыходы (Пример: {'hdmi': 1, 'dp': 2}. В SpEL: videoOutputs['hdmi'] >= 1)", false)
                )),
                Map.entry("CASE", List.of(
                        new FieldMetadataDto("caseType.id", "Long", "ID форм-фактора корпуса", true),
                        new FieldMetadataDto("maxGpuLenMm", "Integer", "Макс. длина видеокарты (мм)", true),
                        new FieldMetadataDto("maxCpuCoolerHeightMm", "Integer", "Макс. высота кулера (мм)", true),
                        new FieldMetadataDto("lengthMm", "Integer", "Длина корпуса (мм)", true),
                        new FieldMetadataDto("widthMm", "Integer", "Ширина корпуса (мм)", true),
                        new FieldMetadataDto("heightMm", "Integer", "Высота корпуса (мм)", true),
                        new FieldMetadataDto("int35Bays", "Integer", "Внутренние отсеки 3.5\"", false),
                        new FieldMetadataDto("ext35Bays", "Integer", "Внешние отсеки 3.5\"", false),
                        new FieldMetadataDto("int25Bays", "Integer", "Внутренние отсеки 2.5\"", false),
                        new FieldMetadataDto("ext525Bays", "Integer", "Внешние отсеки 5.25\"", false),
                        new FieldMetadataDto("expansionSlotsFullHeight", "Integer", "Полноразмерные слоты расширения", false),
                        new FieldMetadataDto("expansionSlotsHalfHeight", "Integer", "Низкопрофильные слоты расширения", false),
                        new FieldMetadataDto("expansionSlotsRiser", "Integer", "Слоты для вертикальной установки GPU", false),
                        new FieldMetadataDto("radiatorSizes", "List<Integer>", "Поддерживаемые размеры радиаторов СЖО (мм)", false),
                        new FieldMetadataDto("fanSizes", "List<Integer>", "Поддерживаемые размеры вентиляторов (мм)", false),
                        new FieldMetadataDto("moboFormFactors", "Set", "Поддерживаемые форм-факторы мат. плат", false),
                        new FieldMetadataDto("frontPanelUsbTypes", "Set", "Разъемы USB на передней панели", false)
                )),
                Map.entry("MEMORY", List.of(
                        new FieldMetadataDto("formFactor.id", "Long", "ID форм-фактора", true),
                        new FieldMetadataDto("memoryType.id", "Long", "ID поколения памяти (DDR)", true),
                        new FieldMetadataDto("modulesCount", "Integer", "Количество модулей в комплекте", false),
                        new FieldMetadataDto("modulesSizeGb", "Integer", "Объем одного модуля (ГБ)", false),
                        new FieldMetadataDto("isEcc", "Boolean", "Поддержка ECC", false),
                        new FieldMetadataDto("isRegistered", "Boolean", "Является ли память буферизованной (Registered)", false)
                )),
                Map.entry("POWER_SUPPLY", List.of(
                        new FieldMetadataDto("type.id", "Long", "ID форм-фактора БП", true),
                        new FieldMetadataDto("modular.id", "Long", "ID типа модульности", true),
                        new FieldMetadataDto("wattageW", "Integer", "Мощность (Вт)", false),
                        new FieldMetadataDto("lengthMm", "Integer", "Длина (мм)", true),
                        new FieldMetadataDto("atx4PinConnectors", "Integer", "Доступно 4-pin ATX (Мат. плата)", false),
                        new FieldMetadataDto("eps8PinConnectors", "Integer", "Доступно 8-pin EPS (Процессор)", false),
                        new FieldMetadataDto("pcie12PinConnectors", "Integer", "Доступно 12-pin PCIe", false),
                        new FieldMetadataDto("pcie6Plus2PinConnectors", "Integer", "Доступно 6+2-pin PCIe", false),
                        new FieldMetadataDto("pcie6PinConnectors", "Integer", "Доступно 6-pin PCIe", false),
                        new FieldMetadataDto("pcie8PinConnectors", "Integer", "Доступно 8-pin PCIe", false),
                        new FieldMetadataDto("pcie12vhpwrConnectors", "Integer", "Доступно 12VHPWR", false),
                        new FieldMetadataDto("sataConnectors", "Integer", "Доступно SATA коннекторов", false),
                        new FieldMetadataDto("molex4PinConnectors", "Integer", "Доступно Molex разъемов (4-pin)", false)
                )),
                Map.entry("CPU_COOLER", List.of(
                        new FieldMetadataDto("isWaterCooled", "Boolean", "Является ли СЖО (водяное охлаждение)", false),
                        new FieldMetadataDto("heightMm", "Integer", "Высота башни (мм)", true),
                        new FieldMetadataDto("waterCooledSizeMm", "Integer", "Размер радиатора СЖО (мм)", true),
                        new FieldMetadataDto("sockets", "Set", "Поддерживаемые сокеты", false)
                )),
                Map.entry("OPTICAL_DRIVE", List.of(
                        new FieldMetadataDto("formFactor.id", "Long", "ID форм-фактора", true),
                        new FieldMetadataDto("storageInterface.id", "Long", "ID интерфейса", true)
                )),
                Map.entry("STORAGE", List.of(
                        new FieldMetadataDto("type.id", "Long", "ID типа накопителя (HDD/SSD)", true),
                        new FieldMetadataDto("formFactor.id", "Long", "ID форм-фактора", true),
                        new FieldMetadataDto("isExternal", "Boolean", "Является ли внешним накопителем", false),
                        new FieldMetadataDto("capacityGb", "Integer", "Емкость (ГБ)", false)
                )),
                Map.entry("CASE_FAN", List.of(
                        new FieldMetadataDto("sizeMm", "Integer", "Размер вентилятора (мм)", false),
                        new FieldMetadataDto("pwm", "Boolean", "Поддержка PWM", false),
                        new FieldMetadataDto("connectors", "Set", "Коннекторы подключения", false)
                )),
                Map.entry("MONITOR", List.of(
                        new FieldMetadataDto("inputHdmi", "Integer", "Порты HDMI", false),
                        new FieldMetadataDto("inputDp", "Integer", "Порты DisplayPort", false),
                        new FieldMetadataDto("inputDvi", "Integer", "Порты DVI", false),
                        new FieldMetadataDto("inputVga", "Integer", "Порты VGA", false),
                        new FieldMetadataDto("inputUsbC", "Integer", "Порты USB Type-C", false),
                        new FieldMetadataDto("inputMiniHdmi", "Integer", "Порты Mini-HDMI", false),
                        new FieldMetadataDto("inputMicroHdmi", "Integer", "Порты Micro-HDMI", false),
                        new FieldMetadataDto("inputMiniDp", "Integer", "Порты Mini-DisplayPort", false),
                        new FieldMetadataDto("inputBnc", "Integer", "Порты BNC", false),
                        new FieldMetadataDto("inputComponent", "Integer", "Порты Component", false),
                        new FieldMetadataDto("inputSVideo", "Integer", "Порты S-Video", false),
                        new FieldMetadataDto("inputVirtualLink", "Integer", "Порты VirtualLink", false)
                )),
                Map.entry("EXPANSION_CARD", List.of(
                        new FieldMetadataDto("cardType", "Enum", "Тип карты расширения (SOUND, WIRED_NETWORK, WIRELESS_NETWORK)", false),
                        new FieldMetadataDto("interfaceType.id", "Long", "ID интерфейса", true)
                ))
        );
    }
}
