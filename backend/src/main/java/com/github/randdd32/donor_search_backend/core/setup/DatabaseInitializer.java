package com.github.randdd32.donor_search_backend.core.setup;

import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
import com.github.randdd32.donor_search_backend.service.auth.UserService;
import com.github.randdd32.donor_search_backend.service.compatibility.CompatibilityRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    private final CompatibilityRuleService ruleService;
    private final UserService userService;

    @Override
    @Transactional
    public void run(String... args) {
        userService.initSuperAdmin();

        if (ruleService.count() == 0) {
            log.info("Initializing compatibility rules...");
            initializeRules();
            log.info("Compatibility rules successfully loaded");
        }
    }

    private void initializeRules() {
        createRule("CPU_SOCKET_MATCH",
                "Совместимость сокета процессора",
                "#ctx.requireCpus().?[socket.id != #ctx.motherboard.socket.id].isEmpty()",
                "Сокет процессора не поддерживается материнской платой",
                "Проверка того, что сокеты всех установленных процессоров совпадают с сокетом материнской платы.",
                Set.of(ComponentType.CPU, ComponentType.MOTHERBOARD));

        createRule("CPU_MAX_MEMORY_LIMIT",
                "Ограничение объема оперативной памяти процессором",
                "#ctx.requireCpus().?[maxMemoryGb.intValue() < #ctx.getTotalRamCapacityGb()].isEmpty()",
                "Суммарный объем оперативной памяти превышает лимит, поддерживаемый процессором",
                "Проверка того, что общий объем оперативной памяти не превышает максимальный объем памяти, поддерживаемый процессором.",
                Set.of(ComponentType.CPU, ComponentType.MEMORY));

        createRule("COOLER_SOCKET_MATCH",
                "Совместимость крепления кулера",
                "#ctx.requireCoolers().?[!#ctx.requireCoolerSockets(#this).![id].contains(#ctx.motherboard.socket.id)].isEmpty()",
                "Крепление кулера не подходит к сокету материнской платы",
                "Проверка того, что крепление всех кулеров поддерживает сокет материнской платы.",
                Set.of(ComponentType.CPU_COOLER, ComponentType.MOTHERBOARD));

        createRule("RAM_TYPE_MATCH",
                "Совместимость типа оперативной памяти",
                "#ctx.requireMemories().?[memoryType.id != #ctx.motherboard.memoryType.id].isEmpty()",
                "Поколение оперативной памяти (DDR) не поддерживается материнской платой",
                "Проверка того, что поколение всех модулей ОЗУ совпадает со слотами на плате.",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD));

        createRule("RAM_CAPACITY_LIMIT",
                "Ограничение объема оперативной памяти",
                "#ctx.getTotalRamCapacityGb() <= #ctx.motherboard.maxMemoryGb.intValue()",
                "Суммарный объем оперативной памяти превышает лимит материнской платы",
                "Проверка того, что общий объем всех модулей ОЗУ не больше заявленного для материнской платы.",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD));

        createRule("RAM_SLOT_LIMIT",
                "Ограничение количества слотов оперативной памяти",
                "#ctx.getTotalRamModules() <= #ctx.motherboard.memorySlots.intValue()",
                "Количество модулей оперативной памяти превышает количество слотов на материнской плате",
                "Проверка того, что общее количество модулей ОЗУ не больше количества слотов, доступных на материнской плате.",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD));

        createRule("RAM_ECC_SUPPORT_MATCH",
                "Поддержка ECC памяти",
                "#ctx.requireMemories().?[isEcc.booleanValue() && !#ctx.isEccSupported()].isEmpty()",
                "Материнская плата или один из процессоров не поддерживают ECC-память",
                "Проверка того, что материнская плата и все установленные процессоры поддерживают серверную память с коррекцией ошибок (ECC).",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD, ComponentType.CPU));

        createRule("GPU_LENGTH_LIMIT",
                "Ограничение длины видеокарты",
                "#ctx.requireGpus().?[lengthMm.intValue() > #ctx.pcCase.maxGpuLenMm.intValue()].isEmpty()",
                "Видеокарта слишком длинная и не поместится в корпус",
                "Проверка того, что каждая из видеокарт поместится в корпус.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.CASE));

        createRule("COOLER_HEIGHT_LIMIT",
                "Ограничение высоты кулера",
                "#ctx.requireCoolers().?[!isWaterCooled.booleanValue() && heightMm.intValue() > #ctx.pcCase.maxCpuCoolerHeightMm.intValue()].isEmpty()",
                "Воздушный кулер слишком высокий: боковая крышка корпуса не закроется",
                "Проверка того, что высота радиаторов воздушных кулеров не слишком велика относительно ширины корпуса.",
                Set.of(ComponentType.CPU_COOLER, ComponentType.CASE));

        createRule("WATER_COOLER_SIZE_MATCH",
                "Поддержка размера радиатора СЖО",
                "#ctx.requireCoolers().?[isWaterCooled.booleanValue() && !#ctx.requireCaseRadiatorSizes().contains(waterCooledSizeMm.intValue())].isEmpty()",
                "Радиатор жидкостного охлаждения данного размера не поддерживается корпусом",
                "Проверка того, что размер радиатора СЖО (например, 240мм, 360мм) входит в список поддерживаемых корпусом.",
                Set.of(ComponentType.CPU_COOLER, ComponentType.CASE));

        createRule("MOBO_FORM_FACTOR_MATCH",
                "Совместимость форм-фактора материнской платы",
                "#ctx.requireCaseMoboFormFactors().![id].contains(#ctx.motherboard.formFactor.id)",
                "Форм-фактор материнской платы не поддерживается корпусом",
                "Проверка того, что материнская плата поместится в корпус.",
                Set.of(ComponentType.MOTHERBOARD, ComponentType.CASE));

        createRule("PSU_POWER_CHECK",
                "Достаточность мощности блока питания",
                "#ctx.getTotalPsuWattage() >= (#ctx.getTotalTdpW() * 1.3)",
                "Мощности блока/ов питания недостаточно для данной конфигурации (CPU + GPU)",
                "Проверка того, что номинала блоков питания достаточно для работы устройства. Для этого рассчитывается TDP сборки (зависит от процессоров и видеокарт) с запасом 30%.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.CPU, ComponentType.VIDEO_CARD));

        createRule("PSU_GPU_PCIE_6_8PIN_CHECK",
                "Наличие 6-pin и 8-pin коннекторов PCIe",
                "#ctx.canPowerGpuPcie6And8PinConnectors()",
                "У блока питания не хватает 6-pin/8-pin кабелей PCIe для видеокарты",
                "Проверка физического наличия необходимых 6-pin и 8-pin коннекторов PCIe у блока питания для подключения питания к видеокарте.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.VIDEO_CARD));

        createRule("PSU_GPU_12VHPWR_CHECK",
                "Наличие коннекторов 12VHPWR",
                "#ctx.getAvailPcie12vhpwr() >= #ctx.getReqPcie12vhpwr()",
                "У блока питания нет разъема 12VHPWR для современных видеокарт",
                "Проверка физического наличия необходимых 12VHPWR коннекторов у блока питания для подключения питания к видеокарте.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.VIDEO_CARD));

        createRule("PSU_GPU_PCIE_12PIN_CHECK",
                "Наличие 12-pin коннекторов PCIe",
                "#ctx.getAvailPcie12Pin() >= #ctx.getReqPcie12Pin()",
                "У блока питания не хватает кабелей 12-pin PCIe для видеокарты",
                "Проверка физического наличия необходимых 12-pin коннекторов PCIe у блока питания для подключения питания к видеокарте.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.VIDEO_CARD));

        createRule("PSU_GPU_EPS_CHECK",
                "Наличие 8-pin коннекторов EPS",
                "#ctx.getAvailEps8Pin() >= #ctx.getReqEps8Pin()",
                "У блока питания не хватает EPS 8-pin кабелей",
                "Проверка физического наличия необходимых EPS 8-pin коннекторов у блока питания.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.VIDEO_CARD));

        createRule("PSU_SATA_POWER_CONNECTORS_CHECK",
                "Наличие SATA-коннекторов питания",
                "#ctx.getSataDevicesCount() <= #ctx.getTotalSataPowerConnectors()",
                "У блока питания не хватает SATA-коннекторов питания для накопителей и оптических приводов",
                "Проверка физического наличия SATA-коннекторов питания у блока питания для подключения внутренних SATA-накопителей и оптических приводов.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.STORAGE, ComponentType.OPTICAL_DRIVE));

        createRule("CASE_35_BAYS_LIMIT",
                "Наличие отсеков 3.5\"",
                "#ctx.getStorageCountByFormFactor('3.5') <= (#ctx.pcCase.int35Bays.intValue() + #ctx.pcCase.ext35Bays.intValue())",
                "Количество жестких дисков 3.5\" превышает количество доступных корзин в корпусе",
                "Проверка того, что количество устанавливаемых накопителей 3.5\" не превышает суммарное количество внутренних и внешних отсеков 3.5\" в корпусе.",
                Set.of(ComponentType.STORAGE, ComponentType.CASE));

        createRule("CASE_TOTAL_STORAGE_LIMIT",
                "Суммарная вместимость накопителей",
                "(#ctx.getStorageCountByFormFactor('3.5') + #ctx.getStorageCountByFormFactor('2.5')) <= (#ctx.pcCase.int35Bays.intValue() + #ctx.pcCase.ext35Bays.intValue() + #ctx.pcCase.int25Bays.intValue())",
                "Суммарное количество накопителей (2.5\" и 3.5\") превышает общую вместимость отсеков корпуса",
                "Проверка того, что суммарное количество всех накопителей 2.5\" и 3.5\" не превышает общее число посадочных мест в корпусе, с учетом возможности установки 2.5\" дисков в 3.5\" слоты.",
                Set.of(ComponentType.STORAGE, ComponentType.CASE));

        createRule("CASE_525_BAYS_LIMIT",
                "Наличие отсеков 5.25\"",
                "#ctx.opticalDrives.size() <= #ctx.pcCase.ext525Bays.intValue()",
                "В корпусе нет отсеков 5.25 для установки оптического привода",
                "Проверка того, хватит ли в корпусе слотов под оптические приводы.",
                Set.of(ComponentType.OPTICAL_DRIVE, ComponentType.CASE));

        createRule("CASE_FAN_SIZE_MATCH",
                "Совместимость размера корпусного вентилятора",
                "#ctx.caseFans.?[!#ctx.requireCaseFanSizes().contains(sizeMm.intValue())].isEmpty()",
                "Размер корпусного вентилятора не поддерживается корпусом",
                "Проверка того, что диаметр всех устанавливаемых вентиляторов входит в список размеров, которые поддерживает корпус.",
                Set.of(ComponentType.CASE_FAN, ComponentType.CASE));

        createRule("CASE_EXPANSION_SLOTS_LIMIT",
                "Ограничение слотов расширения корпуса",
                "#ctx.getTotalGpuCaseExpansionWidth() <= #ctx.pcCase.expansionSlotsFullHeight.intValue()",
                "Количество занимаемых видеокартами слотов превышает количество полноразмерных слотов расширения корпуса",
                "Проверка того, что видеокарты не занимают больше полноразмерных слотов расширения, чем доступно в корпусе.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.CASE));

        createRule("MOBO_SATA_LIMIT",
                "Наличие портов SATA",
                "#ctx.getSataDevicesCount() <= (#ctx.motherboard.sata3Ports.intValue() + #ctx.motherboard.sata6Ports.intValue())",
                "Не хватает SATA портов на материнской плате для всех накопителей и приводов",
                "Проверка того, что количество внутренних SATA-устройств не превышает сумму портов SATA 3Gb/s и SATA 6Gb/s на материнской плате.",
                Set.of(ComponentType.STORAGE, ComponentType.OPTICAL_DRIVE, ComponentType.MOTHERBOARD));

        createRule("MOBO_MINI_PCIE_MSATA_SLOT_LIMIT",
                "Наличие слотов Mini-PCIe/mSATA",
                "#ctx.getMiniPcieMsataDeviceCount() <= #ctx.motherboard.miniPcieMsataSlots.intValue()",
                "Количество Mini-PCIe/mSATA устройств превышает количество соответствующих слотов на материнской плате",
                "Проверка того, что количество mSATA-накопителей и Mini-PCIe карт расширения не превышает количество доступных слотов Mini-PCIe/mSATA на материнской плате.",
                Set.of(ComponentType.STORAGE, ComponentType.EXPANSION_CARD, ComponentType.MOTHERBOARD));

        createRule("MOBO_M2_EXPANSION_CARD_SLOT_MATCH",
                "Совместимость M.2-карт расширения со слотами материнской платы",
                "#ctx.canPlaceM2ExpansionCards()",
                "На материнской плате нет подходящих M.2 E-key слотов для карт расширения",
                "Проверка того, что M.2-карты расширения могут быть размещены в доступных M.2 E-key слотах материнской платы.",
                Set.of(ComponentType.EXPANSION_CARD, ComponentType.MOTHERBOARD));

        createRule("MOBO_M2_STORAGE_SLOT_MATCH",
                "Совместимость M.2-накопителей со слотами материнской платы",
                "#ctx.canPlaceM2Storages()",
                "На материнской плате нет подходящих слотов M.2 для всех накопителей",
                "Проверка того, что количество и типоразмеры M.2-накопителей соответствуют доступным слотам M.2 на материнской плате.",
                Set.of(ComponentType.STORAGE, ComponentType.MOTHERBOARD));

        createRule("MOBO_PCIE_SLOT_ALLOCATION_MATCH",
                "Совместимость PCIe-устройств со слотами материнской платы",
                "#ctx.canPlacePcieDevices()",
                "На материнской плате нет подходящих PCIe-слотов для всех устройств",
                "Проверка того, что видеокарты, PCIe-карты расширения и PCIe-накопители могут быть размещены в доступных слотах PCIe материнской платы.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.EXPANSION_CARD, ComponentType.STORAGE, ComponentType.MOTHERBOARD));

        createRule("MOBO_PCI_SLOT_LIMIT",
                "Наличие слотов PCI",
                "#ctx.getRegularPciDeviceCount() <= #ctx.motherboard.pciSlots.intValue()",
                "Количество PCI-устройств превышает количество PCI-слотов на материнской плате",
                "Проверка того, что количество видеокарт и карт расширения с интерфейсом PCI не превышает количество доступных PCI-слотов на материнской плате.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.EXPANSION_CARD, ComponentType.MOTHERBOARD));

        createRule("LEGACY_GPU_INTERFACE_VERIFIABLE",
                "Проверка устаревших интерфейсов видеокарт",
                "#ctx.canVerifyLegacyGpuInterfaces()",
                "Невозможно проверить совместимость устаревшего интерфейса видеокарты",
                "Проверка того, что интерфейс видеокарты может быть проверен по данным, доступным в модели материнской платы.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.MOTHERBOARD));

        createRule("GPU_GC_HPWR_BACK_CONNECT_MATCH",
                "Поддержка GC-HPWR видеокарты",
                "#ctx.isGcHpwrGpuCompatibleWithMotherboard()",
                "Видеокарта с интерфейсом GC-HPWR требует совместимую материнскую плату с разъемами на обратной стороне",
                "Проверка того, что видеокарта с интерфейсом GC-HPWR устанавливается только с материнской платой, поддерживающей back-connect компоновку.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.MOTHERBOARD));

        createRule("CASE_PSU_COMPATIBILITY",
                "Совместимость габаритов блока питания",
                "!(#ctx.pcCase.caseType.toString() != null and #ctx.pcCase.caseType.name.contains('Mini ITX') and #ctx.requirePsus().?[powerSupplyType.toString() != null and powerSupplyType.name.contains('ATX')].size() > 0)",
                "Стандартный блок питания ATX не поместится в компактный Mini-ITX корпус",
                "Проверка того, что в компактный корпус форм-фактора Mini-ITX не устанавливается крупногабаритный блок питания стандарта ATX.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.CASE));

        createRule("FRONT_USB_2_0_HEADER_MATCH",
                "Поддержка USB 2.0 передней панели",
                "#ctx.getRequiredFrontUsb20Headers() <= (#ctx.motherboard.headerUsb20.intValue() + #ctx.motherboard.headerUsb20SinglePort.intValue())",
                "На материнской плате не хватает внутренних колодок USB 2.0 для передней панели корпуса",
                "Проверка того, что материнская плата имеет достаточное количество внутренних USB 2.0 колодок для разъемов передней панели корпуса.",
                Set.of(ComponentType.MOTHERBOARD, ComponentType.CASE));

        createRule("FRONT_USB_3_2_GEN_1_HEADER_MATCH",
                "Поддержка USB 3.2 Gen 1 передней панели",
                "#ctx.getRequiredFrontUsb32Gen1Headers() <= #ctx.motherboard.headerUsb32Gen1.intValue()",
                "На материнской плате не хватает внутренних колодок USB 3.2 Gen 1 для передней панели корпуса",
                "Проверка того, что материнская плата имеет достаточное количество внутренних USB 3.2 Gen 1 колодок для разъемов передней панели корпуса.",
                Set.of(ComponentType.MOTHERBOARD, ComponentType.CASE));

        createRule("FRONT_USB_3_2_GEN_2_HEADER_MATCH",
                "Поддержка USB 3.2 Gen 2 передней панели",
                "#ctx.getRequiredFrontUsb32Gen2Headers() <= #ctx.motherboard.headerUsb32Gen2.intValue()",
                "На материнской плате не хватает внутренних колодок USB 3.2 Gen 2 для передней панели корпуса",
                "Проверка того, что материнская плата имеет достаточное количество внутренних USB 3.2 Gen 2 колодок для разъемов передней панели корпуса.",
                Set.of(ComponentType.MOTHERBOARD, ComponentType.CASE));

        createRule("FRONT_USB_3_2_GEN_2X2_HEADER_MATCH",
                "Поддержка USB 3.2 Gen 2x2 передней панели",
                "#ctx.getRequiredFrontUsb32Gen2x2Headers() <= #ctx.motherboard.headerUsb32Gen2x2.intValue()",
                "На материнской плате не хватает внутренних колодок USB 3.2 Gen 2x2 для передней панели корпуса",
                "Проверка того, что материнская плата имеет достаточное количество внутренних USB 3.2 Gen 2x2 колодок для разъемов передней панели корпуса.",
                Set.of(ComponentType.MOTHERBOARD, ComponentType.CASE));

        createRule("MONITOR_VIDEO_OUTPUT_MATCH",
                "Совместимость мониторов с видеовыходами",
                "#ctx.canConnectAllMonitorsToGpus()",
                "Не хватает совместимых видеовыходов для подключения всех мониторов",
                "Проверка того, что каждый монитор может быть подключен к одному из доступных видеовыходов видеокарт.",
                Set.of(ComponentType.MONITOR, ComponentType.VIDEO_CARD));
    }

    private void createRule(String code, String name, String expr, String error, String desc, Set<ComponentType> targets) {
        CompatibilityRuleEntity rule = new CompatibilityRuleEntity();
        rule.setRuleCode(code);
        rule.setRuleName(name);
        rule.setExpression(expr);
        rule.setErrorMessage(error);
        rule.setIsActive(true);
        rule.getTargetComponentTypes().addAll(targets);
        rule.setDescription(desc);

        ruleService.create(rule);
    }
}
