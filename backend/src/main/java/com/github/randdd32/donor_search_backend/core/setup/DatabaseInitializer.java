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
                "#ctx.requireCpus().size() > 0 and #ctx.cpus.?[socket.id != #ctx.motherboard.socket.id].isEmpty()",
                "Сокет процессора не поддерживается материнской платой",
                "Проверка того, что сокеты всех установленных процессоров совпадают с сокетом материнской платы.",
                Set.of(ComponentType.CPU, ComponentType.MOTHERBOARD));

        createRule("COOLER_SOCKET_MATCH",
                "Совместимость крепления кулера",
                "#ctx.requireCoolers().size() > 0 and #ctx.coolers.?[!sockets.contains(#ctx.motherboard.socket)].isEmpty()",
                "Крепление кулера не подходит к сокету материнской платы",
                "Проверка того, крепление всех кулеров поддерживает сокет материнской платы.",
                Set.of(ComponentType.CPU_COOLER, ComponentType.MOTHERBOARD));

        createRule("RAM_TYPE_MATCH",
                "Совместимость типа оперативной памяти",
                "#ctx.requireMemories().size() > 0 and #ctx.memories.?[memoryType.id != #ctx.motherboard.memoryType.id].isEmpty()",
                "Поколение оперативной памяти (DDR) не поддерживается материнской платой",
                "Проверка того, что поколение всех модулей ОЗУ совпадает со слотами на плате.",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD));

        createRule("RAM_CAPACITY_LIMIT",
                "Ограничение объема оперативной памяти",
                "#ctx.getTotalRamCapacityGb() <= #ctx.motherboard.maxMemoryGb",
                "Суммарный объем оперативной памяти превышает лимит материнской платы",
                "Проверка того, что общий объем всех модулей ОЗУ не больше заявленного для материнской платы.",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD));

        createRule("RAM_SLOT_LIMIT",
                "Ограничение количества слотов оперативной памяти",
                "#ctx.getTotalRamModules() <= #ctx.motherboard.memorySlots",
                "Количество модулей оперативной памяти превышает количество слотов на материнской плате",
                "Проверка того, что общее количество модулей ОЗУ не больше количества слотов, доступных на материнской плате.",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD));

        createRule("RAM_ECC_SUPPORT_MATCH",
                "Поддержка ECC памяти",
                "#ctx.requireMemories().size() > 0 and #ctx.memories.?[isEcc && !#ctx.isEccSupported()].isEmpty()",
                "Материнская плата или один из процессоров не поддерживают ECC-память",
                "Проверка того, что материнская плата и все установленные процессоры поддерживают серверную память с коррекцией ошибок (ECC).",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD, ComponentType.CPU));

        createRule("GPU_LENGTH_LIMIT",
                "Ограничение длины видеокарты",
                "#ctx.requireGpus().size() > 0 and #ctx.gpus.?[lengthMm > #ctx.pcCase.maxGpuLenMm].isEmpty()",
                "Видеокарта слишком длинная и не поместится в корпус",
                "Проверка того, что каждая из видеокарт поместится в корпус.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.CASE));

        createRule("COOLER_HEIGHT_LIMIT",
                "Ограничение высоты кулера",
                "#ctx.requireCoolers().size() > 0 and #ctx.coolers.?[!isWaterCooled && heightMm > #ctx.pcCase.maxCpuCoolerHeightMm].isEmpty()",
                "Воздушный кулер слишком высокий: боковая крышка корпуса не закроется",
                "Проверка того, что высота радиаторов воздушных кулеров не слишком велика относительно ширины корпуса.",
                Set.of(ComponentType.CPU_COOLER, ComponentType.CASE));

        createRule("WATER_COOLER_SIZE_MATCH",
                "Поддержка размера радиатора СЖО",
                "#ctx.requireCoolers().size() > 0 and #ctx.coolers.?[isWaterCooled && !#ctx.pcCase.radiatorSizes.contains(waterCooledSizeMm)].isEmpty()",
                "Радиатор жидкостного охлаждения данного размера не поддерживается корпусом",
                "Проверка того, что размер радиатора СЖО (например, 240мм, 360мм) входит в список поддерживаемых корпусом.",
                Set.of(ComponentType.CPU_COOLER, ComponentType.CASE));

        createRule("MOBO_FORM_FACTOR_MATCH",
                "Совместимость форм-фактора материнской платы",
                "#ctx.pcCase.moboFormFactors.contains(#ctx.motherboard.formFactor)",
                "Форм-фактор материнской платы не поддерживается корпусом",
                "Проверка того, что материнская плата поместится в корпус.",
                Set.of(ComponentType.MOTHERBOARD, ComponentType.CASE));

        createRule("PSU_POWER_CHECK",
                "Достаточность мощности блока питания",
                "#ctx.getTotalPsuWattage() >= (#ctx.getTotalTdpW() * 1.3)",
                "Мощности блока/ов питания недостаточно для данной конфигурации (CPU + GPU)",
                "Проверка того, что номинала блоков питания достаточно для работы устройства. Для этого рассчитывается TDP сборки (зависит от процессоров и видеокарт) с запасом 30%.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.CPU, ComponentType.VIDEO_CARD));

        createRule("PSU_GPU_8PIN_CHECK",
                "Наличие 8-pin коннекторов PCIe",
                "#ctx.getAvailPcie8Pin() >= #ctx.getReqPcie8Pin()",
                "У блока питания не хватает кабелей 8-pin PCIe для видеокарты",
                "Проверка физического наличия необходимых коннекторов у блока питания для подключения питания к видеокарте.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.VIDEO_CARD));

        createRule("PSU_GPU_6PIN_CHECK",
                "Наличие 6-pin коннекторов PCIe",
                "#ctx.getAvailPcie6Pin() >= #ctx.getReqPcie6Pin()",
                "У блока питания не хватает кабелей 6-pin PCIe для видеокарты",
                "Проверка физического наличия необходимых коннекторов у блока питания для подключения питания к видеокарте.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.VIDEO_CARD));

        createRule("PSU_GPU_12VHPWR_CHECK",
                "Наличие коннекторов 12VHPWR",
                "#ctx.getAvailPcie12vhpwr() >= #ctx.getReqPcie12vhpwr()",
                "У блока питания нет разъема 12VHPWR для современных видеокарт",
                "Проверка физического наличия необходимых коннекторов у блока питания для подключения питания к видеокарте.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.VIDEO_CARD));

        createRule("CASE_35_BAYS_LIMIT",
                "Наличие отсеков 3.5\"",
                "#ctx.getStorageCountByFormFactor('3.5') <= (#ctx.pcCase.int35Bays + #ctx.pcCase.ext35Bays)",
                "Количество жестких дисков 3.5\" превышает количество доступных корзин в корпусе",
                "Проверка того, что количество устанавливаемых накопителей 3.5\" не превышает суммарное количество внутренних и внешних отсеков 3.5\" в корпусе.",
                Set.of(ComponentType.STORAGE, ComponentType.CASE));

        createRule("CASE_TOTAL_STORAGE_LIMIT",
                "Суммарная вместимость накопителей",
                "(#ctx.getStorageCountByFormFactor('3.5') + #ctx.getStorageCountByFormFactor('2.5')) <= (#ctx.pcCase.int35Bays + #ctx.pcCase.ext35Bays + #ctx.pcCase.int25Bays)",
                "Суммарное количество накопителей (2.5\" и 3.5\") превышает общую вместимость отсеков корпуса",
                "Проверка того, что суммарное количество всех накопителей 2.5\" и 3.5\" не превышает общее число посадочных мест в корпусе, с учетом возможности установки 2.5\" дисков в 3.5\" слоты.",
                Set.of(ComponentType.STORAGE, ComponentType.CASE));

        createRule("CASE_525_BAYS_LIMIT",
                "Наличие отсеков 5.25\"",
                "#ctx.opticalDrives.size() <= #ctx.pcCase.ext525Bays",
                "В корпусе нет отсеков 5.25 для установки оптического привода",
                "Проверка того, хватит ли в корпусе слотов под оптические приводы.",
                Set.of(ComponentType.OPTICAL_DRIVE, ComponentType.CASE));

        createRule("CASE_FAN_SIZE_MATCH",
                "Совместимость размера корпусного вентилятора",
                "#ctx.caseFans.?[!#ctx.pcCase.fanSizes.contains(sizeMm)].isEmpty()",
                "Размер корпусного вентилятора не поддерживается корпусом",
                "Проверка того, что диаметр всех устанавливаемых вентиляторов входит в список размеров, которые поддерживает корпус.",
                Set.of(ComponentType.CASE_FAN, ComponentType.CASE));

        createRule("CASE_EXPANSION_SLOTS_LIMIT",
                "Ограничение слотов расширения корпуса",
                "#ctx.getTotalGpuSlotWidth() <= #ctx.pcCase.expansionSlotsFullHeight",
                "Толщина видеокарт превышает количество слотов расширения в корпусе",
                "Проверка того, что суммарная ширина всех видеокарт не выходит за рамки PCIe заглушек корпуса.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.CASE));

        createRule("MOBO_SATA_LIMIT",
                "Наличие портов SATA",
                "#ctx.getSataDevicesCount() <= (#ctx.motherboard.sata3Ports + #ctx.motherboard.sata6Ports)",
                "Не хватает SATA портов на материнской плате для всех накопителей и приводов",
                "Проверка того, что общее количество всех SATA устройств не превышает сумму портов 3Gb/s и 6Gb/s.",
                Set.of(ComponentType.STORAGE, ComponentType.OPTICAL_DRIVE, ComponentType.MOTHERBOARD));

        createRule("MOBO_PCIE_X16_LIMIT",
                "Наличие слотов PCIe x16",
                "#ctx.requireGpus().size() <= #ctx.motherboard.pciX16Slots",
                "Количество видеокарт превышает количество слотов PCIe x16 на материнской плате",
                "Проверка того, что у материнской платы хватает полноразмерных слотов расширения для подключения видеокарт.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.MOTHERBOARD));

        createRule("CASE_PSU_COMPATIBILITY",
                "Совместимость габаритов блока питания",
                "!(#ctx.pcCase.caseType != null and #ctx.pcCase.caseType.name.contains('Mini ITX') and #ctx.psus.?[powerSupplyType != null and powerSupplyType.name.contains('ATX')].size() > 0)",
                "Стандартный блок питания ATX не поместится в компактный Mini-ITX корпус",
                "Проверка того, что в компактный корпус форм-фактора Mini-ITX не устанавливается крупногабаритный блок питания стандарта ATX.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.CASE));
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
