import type { ReactNode } from 'react';
import { Badge } from '../../../../components/ui/Badge/Badge';
import type { DictionaryName } from '../../../../services/dictionary.service';
import type { ExternalComponentCategory } from '../../../../types/integration';

export interface ColumnDef {
  key: string;
  label: string;
  sortable?: boolean;
  render?: (row: Record<string, unknown>) => ReactNode;
}

export type FilterDef =
  | { type: 'dictionary'; key: string; label: string; dictName: DictionaryName }
  | { type: 'boolean'; key: string; label: string }
  | { type: 'static'; key: string; label: string; options: { value: string; label: string }[] }
  | { type: 'range'; key: string; label: string; rangeMinKey?: string; rangeMaxKey?: string }
  | { type: 'number'; key: string; label: string; exactKey: string };

export interface ComponentConfig {
  columns: ColumnDef[];
  filters: FilterDef[];
}

export const renderArray = (arr: unknown) => {
  if (!Array.isArray(arr) || arr.length === 0) return <span style={{ color: 'var(--text-muted)' }}>—</span>;
  if (arr.length <= 2) return arr.join(', ');
  return <span title={arr.join(', ')}>{arr[0]}, {arr[1]} <Badge variant="default">+{arr.length - 2}</Badge></span>;
};

const COMMON_COLUMNS: ColumnDef[] =[
  { key: 'id', label: 'ID', sortable: true },
  { key: 'name', label: 'Название', sortable: true },
  { key: 'manufacturerName', label: 'Производитель' },
  { key: 'partNumbers', label: 'Part Numbers', render: (r) => renderArray(r.partNumbers) },
];

const COMMON_FILTERS: FilterDef[] =[
  { key: 'manufacturerIds', label: 'Производитель', type: 'dictionary', dictName: 'hwManufacturers' },
];

const renderBoolBadge = (val: unknown) => 
  val ? <Badge variant="info">Да</Badge> : <Badge variant="danger">Нет</Badge>;

export const COMPONENT_REGISTRY: Record<ExternalComponentCategory | 'DEFAULT', ComponentConfig> = {
  CPU: {
    columns:[
      ...COMMON_COLUMNS,
      { key: 'coreCount', label: 'Ядра', sortable: true },
      { key: 'coreClockGhz', label: 'Частота (ГГц)', sortable: true },
      { key: 'tdpW', label: 'TDP (Вт)', sortable: true },
      { key: 'eccSupport', label: 'ECC', sortable: true, render: (r) => renderBoolBadge(r.eccSupport) },
      { key: 'socketName', label: 'Сокет' },
      { key: 'microarchitectureName', label: 'Микроархитектура' },
      { key: 'graphicsName', label: 'Встроенная графика' }
    ],
    filters:[
      ...COMMON_FILTERS,
      { key: 'socketIds', label: 'Сокет', type: 'dictionary', dictName: 'cpuSockets' },
      { key: 'microarchitectureIds', label: 'Микроархитектура', type: 'dictionary', dictName: 'microarchitectures' },
      { key: 'graphicsIds', label: 'Встр. графика', type: 'dictionary', dictName: 'integratedGraphics' },
      { key: 'cores', label: 'Кол-во ядер', type: 'range', rangeMinKey: 'minCoreCount', rangeMaxKey: 'maxCoreCount' },
      { key: 'clock', label: 'Частота (ГГц)', type: 'range', rangeMinKey: 'minCoreClock', rangeMaxKey: 'maxCoreClock' },
      { key: 'tdp', label: 'TDP (Вт)', type: 'range', rangeMinKey: 'minTdp', rangeMaxKey: 'maxTdp' },
      { key: 'eccSupport', label: 'Поддержка ECC', type: 'boolean' }
    ],
  },
  CPU_COOLER: {
    columns:[
      ...COMMON_COLUMNS,
      { key: 'isWaterCooled', label: 'СЖО', sortable: true, render: (r) => renderBoolBadge(r.isWaterCooled) },
      { key: 'heightMm', label: 'Высота (мм)', sortable: true },
      { key: 'waterCooledSizeMm', label: 'Размер СЖО (мм)', sortable: true },
      { key: 'rpmMax', label: 'Макс. RPM', sortable: true },
      { key: 'sockets', label: 'Сокеты', render: (r) => renderArray(r.sockets) },
      { key: 'colorName', label: 'Цвет' }
    ],
    filters:[
      ...COMMON_FILTERS,
      { key: 'colorIds', label: 'Цвет', type: 'dictionary', dictName: 'colors' },
      { key: 'socketIds', label: 'Сокеты', type: 'dictionary', dictName: 'cpuSockets' },
      { key: 'isWaterCooled', label: 'Водяное охлаждение', type: 'boolean' },
      { key: 'height', label: 'Высота (мм)', type: 'range', rangeMinKey: 'minHeight', rangeMaxKey: 'maxHeight' },
      { key: 'waterSize', label: 'Размер СЖО (мм)', type: 'range', rangeMinKey: 'minWaterSize', rangeMaxKey: 'maxWaterSize' },
      { key: 'rpm', label: 'Обороты (RPM)', type: 'range', rangeMinKey: 'minRpm', rangeMaxKey: 'maxRpm' }
    ],
  },
  MOTHERBOARD: {
    columns:[
      ...COMMON_COLUMNS,
      { key: 'socketName', label: 'Сокет' },
      { key: 'formFactorName', label: 'Форм-фактор' },
      { key: 'memoryTypeName', label: 'Тип ОЗУ' },
      { key: 'maxMemoryGb', label: 'Макс ОЗУ (ГБ)', sortable: true },
      { key: 'memorySlots', label: 'Кол-во слотов ОЗУ', sortable: true },
      { key: 'memorySpeedMaxMhz', label: 'Макс. скорость ОЗУ (МГц)', sortable: true },
      { key: 'eccSupport', label: 'Поддержка ECC', render: (r) => renderBoolBadge(r.eccSupport) },
      { key: 'usesBackConnect', label: 'Разъемы сзади', render: (r) => renderBoolBadge(r.usesBackConnect) }
    ],
    filters:[
      ...COMMON_FILTERS,
      { key: 'socketIds', label: 'Сокет', type: 'dictionary', dictName: 'cpuSockets' },
      { key: 'formFactorIds', label: 'Форм-фактор', type: 'dictionary', dictName: 'motherboardFormFactors' },
      { key: 'memoryTypeIds', label: 'Тип ОЗУ', type: 'dictionary', dictName: 'memoryTypes' },
      { key: 'maxMemoryGb', label: 'Максимальный объем ОЗУ (ГБ) от', type: 'range', rangeMinKey: 'minMaxMemoryGb' },
      { key: 'memorySlots', label: 'Слотов ОЗУ от', type: 'range', rangeMinKey: 'minMemorySlots' },
      { key: 'memorySpeed', label: 'Скорость ОЗУ (МГц) от', type: 'range', rangeMinKey: 'minMemorySpeedMhz' },
      { key: 'eccSupport', label: 'Поддержка ECC', type: 'boolean' },
      { key: 'usesBackConnect', label: 'Разъемы сзади', type: 'boolean' }
    ],
  },
  MEMORY: {
    columns:[
      ...COMMON_COLUMNS,
      { key: 'memoryTypeName', label: 'Тип' },
      { key: 'formFactorName', label: 'Форм-фактор' },
      { key: 'colorName', label: 'Цвет' },
      { key: 'frequencyMhz', label: 'Частота (МГц)', sortable: true },
      { key: 'modulesCount', label: 'Кол-во модулей', sortable: true },
      { key: 'modulesSizeGb', label: 'Объем модуля (ГБ)', sortable: true },
      { key: 'casLatency', label: 'CAS Latency', sortable: true },
      { key: 'isEcc', label: 'ECC', render: (r) => renderBoolBadge(r.isEcc) },
      { key: 'isRegistered', label: 'Буферизованная (Reg)', render: (r) => renderBoolBadge(r.isRegistered) }

    ],
    filters:[
      ...COMMON_FILTERS,
      { key: 'memoryTypeIds', label: 'Тип памяти', type: 'dictionary', dictName: 'memoryTypes' },
      { key: 'formFactorIds', label: 'Форм-фактор', type: 'dictionary', dictName: 'ramFormFactors' },
      { key: 'colorIds', label: 'Цвет', type: 'dictionary', dictName: 'colors' },
      { key: 'freq', label: 'Частота (МГц)', type: 'range', rangeMinKey: 'minFrequency', rangeMaxKey: 'maxFrequency' },
      { key: 'count', label: 'Кол-во модулей', type: 'range', rangeMinKey: 'minModulesCount', rangeMaxKey: 'maxModulesCount' },
      { key: 'size', label: 'Объем модуля (ГБ)', type: 'range', rangeMinKey: 'minModulesSize', rangeMaxKey: 'maxModulesSize' },
      { key: 'cas', label: 'CAS Latency', type: 'range', rangeMinKey: 'minCas', rangeMaxKey: 'maxCas' },
      { key: 'isEcc', label: 'ECC', type: 'boolean' },
      { key: 'isRegistered', label: 'Буферизованная (Reg)', type: 'boolean' }
    ],
  },
  VIDEO_CARD: {
    columns:[
      ...COMMON_COLUMNS,
      { key: 'chipsetName', label: 'Чипсет' },
      { key: 'memoryTypeName', label: 'Тип памяти' },
      { key: 'memoryGb', label: 'Память (ГБ)', sortable: true },
      { key: 'lengthMm', label: 'Длина (мм)', sortable: true },
      { key: 'tdpW', label: 'TDP (Вт)', sortable: true },
      { key: 'slotWidth', label: 'Ширина (в слотах)', sortable: true }
    ],
    filters:[
      ...COMMON_FILTERS,
      { key: 'chipsetIds', label: 'Чипсет', type: 'dictionary', dictName: 'gpuChipsets' },
      { key: 'memoryTypeIds', label: 'Тип памяти', type: 'dictionary', dictName: 'memoryTypes' },
      { key: 'length', label: 'Длина (мм)', type: 'range', rangeMinKey: 'minLength', rangeMaxKey: 'maxLength' },
      { key: 'tdp', label: 'TDP (Вт)', type: 'range', rangeMinKey: 'minTdp', rangeMaxKey: 'maxTdp' },
      { key: 'slotWidth', label: 'Ширина (в слотах)', type: 'number', exactKey: 'slotWidth' },
    ],
  },
  STORAGE: {
    columns:[
      ...COMMON_COLUMNS,
      { key: 'typeName', label: 'Тип (HDD/SSD)' },
      { key: 'formFactorName', label: 'Форм-фактор' },
      { key: 'colorName', label: 'Цвет' },
      { key: 'interfaces', label: 'Интерфейсы', render: (r) => renderArray(r.interfaces) },
      { key: 'capacityGb', label: 'Емкость (ГБ)', sortable: true },
      { key: 'cacheMb', label: 'Кэш (МБ)', sortable: true },
      { key: 'rpm', label: 'Скорость шпинделя (RPM)', sortable: true },
      { key: 'isExternal', label: 'Внешний', render: (r) => renderBoolBadge(r.isExternal) }
    ],
    filters:[
      ...COMMON_FILTERS,
      { key: 'typeIds', label: 'Тип накопителя', type: 'dictionary', dictName: 'storageTypes' },
      { key: 'formFactorIds', label: 'Форм-фактор', type: 'dictionary', dictName: 'storageFormFactors' },
      { key: 'colorIds', label: 'Цвет', type: 'dictionary', dictName: 'colors' },
      { key: 'interfaceIds', label: 'Интерфейсы', type: 'dictionary', dictName: 'storageInterfaces' },
      { key: 'capacity', label: 'Емкость (ГБ)', type: 'range', rangeMinKey: 'minCapacity', rangeMaxKey: 'maxCapacity' },
      { key: 'cache', label: 'Кэш (МБ)', type: 'range', rangeMinKey: 'minCache', rangeMaxKey: 'maxCache' },
      { key: 'rpm', label: 'Скорость шпинделя (RPM)', type: 'range', rangeMinKey: 'minRpm', rangeMaxKey: 'maxRpm' },
      { key: 'isExternal', label: 'Внешний диск', type: 'boolean' },
    ],
  },
  POWER_SUPPLY: {
    columns:[
      ...COMMON_COLUMNS,
      { key: 'typeName', label: 'Тип БП' },
      { key: 'efficiencyName', label: 'Сертификат' },
      { key: 'modularName', label: 'Модульность' },
      { key: 'colorName', label: 'Цвет' },
      { key: 'wattageW', label: 'Мощность (Вт)', sortable: true },
      { key: 'lengthMm', label: 'Длина (мм)', sortable: true }
    ],
    filters:[
      ...COMMON_FILTERS,
      { key: 'typeIds', label: 'Тип БП', type: 'dictionary', dictName: 'powerSupplyTypes' },
      { key: 'efficiencyIds', label: 'Сертификат', type: 'dictionary', dictName: 'efficiencyRatings' },
      { key: 'modularIds', label: 'Модульность', type: 'dictionary', dictName: 'modularTypes' },
      { key: 'colorIds', label: 'Цвет', type: 'dictionary', dictName: 'colors' },
      { key: 'wattage', label: 'Мощность (Вт)', type: 'range', rangeMinKey: 'minWattage', rangeMaxKey: 'maxWattage' },
      { key: 'length', label: 'Длина (мм)', type: 'range', rangeMinKey: 'minLength', rangeMaxKey: 'maxLength' },
    ],
  },
  CASE: {
    columns: [
      ...COMMON_COLUMNS,
      { key: 'caseTypeName', label: 'Тип корпуса' },
      { key: 'colorName', label: 'Цвет' },
      { key: 'sidePanelName', label: 'Боковая панель' },
      { key: 'lengthMm', label: 'Длина (мм)', sortable: true },
      { key: 'widthMm', label: 'Ширина (мм)', sortable: true },
      { key: 'heightMm', label: 'Высота (мм)', sortable: true },
      { key: 'maxGpuLenMm', label: 'Макс. длина GPU', sortable: true },
      { key: 'maxCpuCoolerHeightMm', label: 'Макс. высота кулера', sortable: true },
      { key: 'int35Bays', label: 'Отсеки 3.5"', sortable: true },
      { key: 'expansionSlotsFullHeight', label: 'Слоты расширения', sortable: true },
      { key: 'moboFormFactors', label: 'Поддержка мат. плат', render: (r) => renderArray(r.moboFormFactors) },
      { key: 'frontPanelUsbTypes', label: 'USB на панели', render: (r) => renderArray(r.frontPanelUsbTypes) }
    ],
    filters: [
      ...COMMON_FILTERS,
      { key: 'caseTypeIds', label: 'Тип корпуса', type: 'dictionary', dictName: 'caseTypes' },
      { key: 'colorIds', label: 'Цвет', type: 'dictionary', dictName: 'colors' },
      { key: 'sidePanelIds', label: 'Боковая панель', type: 'dictionary', dictName: 'sidePanels' },
      { key: 'moboFormFactorIds', label: 'Поддержка мат. плат', type: 'dictionary', dictName: 'motherboardFormFactors' },
      { key: 'frontPanelUsbIds', label: 'USB на панели', type: 'dictionary', dictName: 'frontPanelUsbs' },
      { key: 'length', label: 'Длина (мм)', type: 'range', rangeMinKey: 'minLength', rangeMaxKey: 'maxLength' },
      { key: 'width', label: 'Ширина (мм)', type: 'range', rangeMinKey: 'minWidth', rangeMaxKey: 'maxWidth' },
      { key: 'height', label: 'Высота (мм)', type: 'range', rangeMinKey: 'minHeight', rangeMaxKey: 'maxHeight' },
      { key: 'bays35', label: 'Внутренние отсеки 3.5" (от)', type: 'range', rangeMinKey: 'minInt35Bays' },
      { key: 'expSlots', label: 'Слоты расширения (от)', type: 'range', rangeMinKey: 'minExpansionSlots' },
    ],
  },
  CASE_FAN: {
    columns: [
      ...COMMON_COLUMNS,
      { key: 'sizeMm', label: 'Размер (мм)', sortable: true },
      { key: 'colorName', label: 'Цвет' },
      { key: 'pwm', label: 'PWM', render: (r) => renderBoolBadge(r.pwm) },
      { key: 'rpmMax', label: 'Макс. RPM', sortable: true },
      { key: 'airflowMax', label: 'Воздушный поток (CFM)', sortable: true },
      { key: 'connectors', label: 'Коннекторы', render: (r) => renderArray(r.connectors) }
    ],
    filters: [
      ...COMMON_FILTERS,
      { key: 'colorIds', label: 'Цвет', type: 'dictionary', dictName: 'colors' },
      { key: 'connectorIds', label: 'Коннекторы', type: 'dictionary', dictName: 'fanConnectors' },
      { key: 'pwm', label: 'Поддержка PWM', type: 'boolean' },
      { key: 'rpm', label: 'Обороты (RPM)', type: 'range', rangeMinKey: 'minRpm', rangeMaxKey: 'maxRpm' },
      { key: 'airflow', label: 'Воздушный поток (CFM)', type: 'range', rangeMinKey: 'minAirflow', rangeMaxKey: 'maxAirflow' },
    ],
  },
  OPTICAL_DRIVE: {
    columns: [
      ...COMMON_COLUMNS,
      { key: 'formFactorName', label: 'Форм-фактор' },
      { key: 'interfaceName', label: 'Интерфейс' },
    ],
    filters: [
      ...COMMON_FILTERS,
      { key: 'formFactorIds', label: 'Форм-фактор', type: 'dictionary', dictName: 'opticalDriveFormFactors' },
      { key: 'interfaceIds', label: 'Интерфейс', type: 'dictionary', dictName: 'storageInterfaces' },
    ],
  },
  MONITOR: {
    columns: [
      ...COMMON_COLUMNS,
      { key: 'screenSizeIn', label: 'Диагональ', sortable: true },
      { key: 'resolutionName', label: 'Разрешение' },
      { key: 'aspectRatioName', label: 'Соотношение сторон' },
      { key: 'refreshRateHz', label: 'Герцовка', sortable: true },
      { key: 'responseTimeMs', label: 'Отклик (мс)', sortable: true },
      { key: 'panelTypeName', label: 'Матрица' },
    ],
    filters: [
      ...COMMON_FILTERS,
      { key: 'resolutionIds', label: 'Разрешение', type: 'dictionary', dictName: 'monitorResolutions' },
      { key: 'panelTypeIds', label: 'Тип матрицы', type: 'dictionary', dictName: 'panelTypes' },
      { key: 'aspectRatioIds', label: 'Соотношение сторон', type: 'dictionary', dictName: 'aspectRatios' },
      { key: 'size', label: 'Диагональ (дюймы)', type: 'range', rangeMinKey: 'minScreenSize', rangeMaxKey: 'maxScreenSize' },
      { key: 'refresh', label: 'Частота (Гц)', type: 'range', rangeMinKey: 'minRefreshRate', rangeMaxKey: 'maxRefreshRate' },
      { key: 'responseTime', label: 'Отклик (мс)', type: 'range', rangeMinKey: 'minResponseTime', rangeMaxKey: 'maxResponseTime' },
    ],
  },
  EXPANSION_CARD: {
    columns: [
      ...COMMON_COLUMNS,
      { key: 'cardType', label: 'Категория карты' },
      { key: 'interfaceName', label: 'Интерфейс' },
      { key: 'colorName', label: 'Цвет' },
      { key: 'audioChipsetName', label: 'Аудиочипсет' },
      { key: 'protocolName', label: 'Wi-Fi протокол' },
      { key: 'channels', label: 'Аудиоканалы', sortable: true },
      { key: 'digitalAudioBit', label: 'Битность аудио', sortable: true },
      { key: 'sampleRateKhz', label: 'Частота (кГц)', sortable: true }
    ],
    filters: [
      ...COMMON_FILTERS,
      { key: 'cardType', label: 'Категория', type: 'static', options: [
        { value: 'SOUND', label: 'Звуковая карта' },
        { value: 'WIRED_NETWORK', label: 'Сетевая (LAN)' },
        { value: 'WIRELESS_NETWORK', label: 'Wi-Fi адаптер' },
      ]},
      { key: 'interfaceIds', label: 'Интерфейс подключения', type: 'dictionary', dictName: 'expansionInterfaces' },
      { key: 'colorIds', label: 'Цвет', type: 'dictionary', dictName: 'colors' },
      { key: 'audioChipsetIds', label: 'Аудиочипсет', type: 'dictionary', dictName: 'audioChipsets' },
      { key: 'protocolIds', label: 'Wi-Fi протокол', type: 'dictionary', dictName: 'wirelessProtocols' },
      { key: 'channels', label: 'Аудиоканалы', type: 'range', rangeMinKey: 'minChannels', rangeMaxKey: 'maxChannels' },
      { key: 'bitRate', label: 'Битность аудио', type: 'range', rangeMinKey: 'minDigitalAudioBit', rangeMaxKey: 'maxDigitalAudioBit' },
      { key: 'sampleRate', label: 'Частота дискретизации (кГц)', type: 'range', rangeMinKey: 'minSampleRateKhz', rangeMaxKey: 'maxSampleRateKhz' }
    ],
  },
  UNKNOWN: { columns: COMMON_COLUMNS, filters: COMMON_FILTERS },
  DEFAULT: { columns: COMMON_COLUMNS, filters: COMMON_FILTERS }
};