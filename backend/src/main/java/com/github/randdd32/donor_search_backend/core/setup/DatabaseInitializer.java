package com.github.randdd32.donor_search_backend.core.setup;

import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.model.enums.ComponentType;
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

    @Override
    @Transactional
    public void run(String... args) {
        if (ruleService.count() == 0) {
            log.info("Initializing compatibility rules...");
            initializeRules();
            log.info("Compatibility rules successfully loaded");
        }
    }

    private void initializeRules() {
        createRule("CPU_SOCKET_MATCH",
                "#ctx.requireCpus().size() > 0 and #ctx.cpus.?[socket.id != #ctx.motherboard.socket.id].isEmpty()",
                "Сокет процессора не поддерживается материнской платой",
                "Сверяет сокеты всех установленных процессоров с сокетом материнской платы.",
                Set.of(ComponentType.CPU, ComponentType.MOTHERBOARD));

        createRule("COOLER_SOCKET_MATCH",
                "#ctx.requireCoolers().size() > 0 and #ctx.coolers.?[!sockets.contains(#ctx.motherboard.socket)].isEmpty()",
                "Крепление кулера не подходит к сокету материнской платы",
                "Крепление всех кулеров должно поддерживать текущий сокет материнской платы.",
                Set.of(ComponentType.CPU_COOLER, ComponentType.MOTHERBOARD));

        createRule("RAM_TYPE_MATCH",
                "#ctx.requireMemories().size() > 0 and #ctx.memories.?[memoryType.id != #ctx.motherboard.memoryType.id].isEmpty()",
                "Поколение оперативной памяти (DDR) не поддерживается материнской платой",
                "Поколение всех модулей ОЗУ должно совпадать со слотами на плате.",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD));

        createRule("RAM_CAPACITY_LIMIT",
                "#ctx.getTotalRamCapacityGb() <= #ctx.motherboard.maxMemoryGb",
                "Суммарный объем оперативной памяти превышает лимит материнской платы",
                "Проверка того, что общий объем всех плашек ОЗУ не больше заявленного платой.",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD));

        createRule("RAM_SLOT_LIMIT",
                "#ctx.getTotalRamModules() <= #ctx.motherboard.memorySlots",
                "Количество модулей оперативной памяти превышает количество слотов на материнской плате",
                "Физическое ограничение числа плашек ОЗУ.",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD));

        createRule("RAM_ECC_SUPPORT_MATCH",
                "#ctx.requireMemories().size() > 0 and #ctx.memories.?[isEcc && !#ctx.isEccSupported()].isEmpty()",
                "Материнская плата или один из процессоров не поддерживают ECC-память",
                "Серверная память с коррекцией ошибок (ECC) требует поддержки от материнской платы и всех установленных процессоров.",
                Set.of(ComponentType.MEMORY, ComponentType.MOTHERBOARD, ComponentType.CPU));

        createRule("GPU_LENGTH_LIMIT",
                "#ctx.requireGpus().size() > 0 and #ctx.gpus.?[lengthMm > #ctx.pcCase.maxGpuLenMm].isEmpty()",
                "Видеокарта слишком длинная и не поместится в корпус",
                "Длина каждой установленной видеокарты проверяется относительно корпуса.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.CASE));

        createRule("COOLER_HEIGHT_LIMIT",
                "#ctx.requireCoolers().size() > 0 and #ctx.coolers.?[!isWaterCooled && heightMm > #ctx.pcCase.maxCpuCoolerHeightMm].isEmpty()",
                "Воздушный кулер слишком высокий: боковая крышка корпуса не закроется",
                "Высота радиаторов воздушных кулеров проверяется относительно ширины корпуса.",
                Set.of(ComponentType.CPU_COOLER, ComponentType.CASE));

        createRule("WATER_COOLER_SIZE_MATCH",
                "#ctx.requireCoolers().size() > 0 and #ctx.coolers.?[isWaterCooled && !#ctx.pcCase.radiatorSizes.contains(waterCooledSizeMm)].isEmpty()",
                "Радиатор жидкостного охлаждения данного размера не поддерживается корпусом",
                "Размер радиатора СЖО (например, 240мм, 360мм) должен входить в список поддерживаемых корпусом.",
                Set.of(ComponentType.CPU_COOLER, ComponentType.CASE));

        createRule("MOBO_FORM_FACTOR_MATCH",
                "#ctx.pcCase.moboFormFactors.contains(#ctx.motherboard.formFactor)",
                "Форм-фактор материнской платы не поддерживается корпусом",
                "Например, плата ATX не поместится в корпус Mini-ITX.",
                Set.of(ComponentType.MOTHERBOARD, ComponentType.CASE));

        createRule("PSU_POWER_CHECK",
                "#ctx.getTotalPsuWattage() >= (#ctx.getTotalTdpW() * 1.3 + 50)",
                "Мощности блока/ов питания недостаточно для данной конфигурации (CPU + GPU)",
                "Проверка расчетного TDP сборки с запасом 30% (плюс 50 для периферии) против номинала блоков питания.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.CPU, ComponentType.VIDEO_CARD));

        createRule("PSU_GPU_8PIN_CHECK",
                "#ctx.getAvailPcie8Pin() >= #ctx.getReqPcie8Pin()",
                "У блока питания не хватает кабелей 8-pin PCIe для видеокарты",
                "Проверка физического наличия необходимых коннекторов.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.VIDEO_CARD));

        createRule("PSU_GPU_6PIN_CHECK",
                "#ctx.getAvailPcie6Pin() >= #ctx.getReqPcie6Pin()",
                "У блока питания не хватает кабелей 6-pin PCIe для видеокарты",
                "Проверка физического наличия необходимых коннекторов.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.VIDEO_CARD));

        createRule("PSU_GPU_12VHPWR_CHECK",
                "#ctx.getAvailPcie12vhpwr() >= #ctx.getReqPcie12vhpwr()",
                "У блока питания нет разъема 12VHPWR для современных видеокарт",
                "Проверка наличия разъемов стандарта 12V-2x6.",
                Set.of(ComponentType.POWER_SUPPLY, ComponentType.VIDEO_CARD));

        createRule("CASE_35_BAYS_LIMIT",
                "#ctx.getStorageCountByFormFactor('3,5') <= (#ctx.pcCase.int35Bays + #ctx.pcCase.ext35Bays)",
                "Количество жестких дисков 3.5 превышает количество доступных корзин в корпусе",
                "Хватит ли места для накопителей с форм-фактором 3.5\".",
                Set.of(ComponentType.STORAGE, ComponentType.CASE));

        createRule("CASE_25_BAYS_LIMIT",
                "#ctx.getStorageCountByFormFactor('2,5') <= #ctx.pcCase.int25Bays",
                "Количество накопителей 2.5 превышает количество отсеков в корпусе",
                "Хватит ли места для накопителей с форм-фактором 2.5\".",
                Set.of(ComponentType.STORAGE, ComponentType.CASE));

        createRule("CASE_525_BAYS_LIMIT",
                "#ctx.opticalDrives.size() <= #ctx.pcCase.ext525Bays",
                "В корпусе нет отсеков 5.25 для установки оптического привода",
                "Современные корпуса часто не имеют слотов под DVD-приводы.",
                Set.of(ComponentType.OPTICAL_DRIVE, ComponentType.CASE));

        createRule("CASE_FAN_SIZE_MATCH",
                "#ctx.caseFans.?[!#ctx.pcCase.fanSizes.contains(sizeMm)].isEmpty()",
                "Размер корпусного вентилятора не поддерживается корпусом",
                "Диаметр всех устанавливаемых вентиляторов должен быть в списке поддерживаемых корпусом.",
                Set.of(ComponentType.CASE_FAN, ComponentType.CASE));

        createRule("CASE_EXPANSION_SLOTS_LIMIT",
                "#ctx.getTotalGpuSlotWidth() <= #ctx.pcCase.expansionSlotsFullHeight",
                "Толщина видеокарт превышает количество слотов расширения в корпусе",
                "Суммарная ширина всех видеокарт не должна выходить за рамки PCIe заглушек корпуса.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.CASE));

        createRule("MOBO_SATA_LIMIT",
                "#ctx.getSataDevicesCount() <= (#ctx.motherboard.sata3Ports + #ctx.motherboard.sata6Ports)",
                "Не хватает SATA портов на материнской плате для всех накопителей и приводов",
                "Сумма всех SATA устройств не должна превышать сумму портов 3Gb/s и 6Gb/s.",
                Set.of(ComponentType.STORAGE, ComponentType.OPTICAL_DRIVE, ComponentType.MOTHERBOARD));

        createRule("MOBO_PCIE_X16_LIMIT",
                "#ctx.requireGpus().size() <= #ctx.motherboard.pciX16Slots",
                "Количество видеокарт превышает количество слотов PCIe x16 на материнской плате",
                "Невозможно вставить GPU, если нет полноразмерных слотов расширения.",
                Set.of(ComponentType.VIDEO_CARD, ComponentType.MOTHERBOARD));
    }

    private void createRule(String code, String expr, String error, String desc, Set<ComponentType> targets) {
        CompatibilityRuleEntity rule = new CompatibilityRuleEntity();
        rule.setRuleCode(code);
        rule.setExpression(expr);
        rule.setErrorMessage(error);
        rule.setIsActive(true);
        rule.getTargetComponentTypes().addAll(targets);
        rule.setDescription(desc);

        ruleService.create(rule);
    }
}
