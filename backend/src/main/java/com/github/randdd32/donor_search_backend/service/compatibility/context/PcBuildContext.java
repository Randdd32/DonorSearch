package com.github.randdd32.donor_search_backend.service.compatibility.context;

import com.github.randdd32.donor_search_backend.core.error.MissingContextDataException;
import com.github.randdd32.donor_search_backend.model.dictionary.CpuSocketEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.MotherboardFormFactorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.StorageInterfaceEntity;
import com.github.randdd32.donor_search_backend.model.hardware.CaseEntity;
import com.github.randdd32.donor_search_backend.model.hardware.CaseFanEntity;
import com.github.randdd32.donor_search_backend.model.hardware.ComponentEntity;
import com.github.randdd32.donor_search_backend.model.hardware.CpuCoolerEntity;
import com.github.randdd32.donor_search_backend.model.hardware.CpuEntity;
import com.github.randdd32.donor_search_backend.model.hardware.ExpansionCardEntity;
import com.github.randdd32.donor_search_backend.model.hardware.MemoryEntity;
import com.github.randdd32.donor_search_backend.model.hardware.MonitorEntity;
import com.github.randdd32.donor_search_backend.model.hardware.MotherboardEntity;
import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import com.github.randdd32.donor_search_backend.model.hardware.PowerSupplyEntity;
import com.github.randdd32.donor_search_backend.model.hardware.StorageEntity;
import com.github.randdd32.donor_search_backend.model.hardware.VideoCardEntity;
import com.github.randdd32.donor_search_backend.model.hardware.nested.M2Slot;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Getter
@Setter
public class PcBuildContext {
    private CaseEntity pcCase;
    private MotherboardEntity motherboard;
    private List<PowerSupplyEntity> psus = new ArrayList<>();
    private List<CpuEntity> cpus = new ArrayList<>();
    private List<CpuCoolerEntity> coolers = new ArrayList<>();
    private List<VideoCardEntity> gpus = new ArrayList<>();
    private List<MemoryEntity> memories = new ArrayList<>();
    private List<StorageEntity> storages = new ArrayList<>();
    private List<ExpansionCardEntity> expansionCards = new ArrayList<>();
    private List<CaseFanEntity> caseFans = new ArrayList<>();
    private List<OpticalDriveEntity> opticalDrives = new ArrayList<>();
    private List<MonitorEntity> monitors = new ArrayList<>();

    private static final Pattern M2_FORM_FACTOR_PATTERN = Pattern.compile("(?i)M\\.2-(\\d+)");
    private static final Pattern PCIE_INTERFACE_PATTERN = Pattern.compile("(?i)^PCIe\\s*x(\\d+).*");

    public PcBuildContext copy() {
        PcBuildContext copy = new PcBuildContext();
        copy.setPcCase(this.pcCase);
        copy.setMotherboard(this.motherboard);
        copy.getPsus().addAll(this.psus);
        copy.getCpus().addAll(this.cpus);
        copy.getCoolers().addAll(this.coolers);
        copy.getGpus().addAll(this.gpus);
        copy.getMemories().addAll(this.memories);
        copy.getStorages().addAll(this.storages);
        copy.getExpansionCards().addAll(this.expansionCards);
        copy.getCaseFans().addAll(this.caseFans);
        copy.getOpticalDrives().addAll(this.opticalDrives);
        copy.getMonitors().addAll(this.monitors);
        return copy;
    }

    public void putComponent(ComponentEntity component) {
        if (component instanceof CaseEntity c) {
            this.pcCase = c;
        } else if (component instanceof MotherboardEntity m) {
            this.motherboard = m;
        } else if (component instanceof PowerSupplyEntity p) {
            this.psus.add(p);
        } else if (component instanceof CpuEntity c) {
            this.cpus.add(c);
        } else if (component instanceof CpuCoolerEntity c) {
            this.coolers.add(c);
        } else if (component instanceof VideoCardEntity v) {
            this.gpus.add(v);
        } else if (component instanceof MemoryEntity m) {
            this.memories.add(m);
        } else if (component instanceof StorageEntity s) {
            this.storages.add(s);
        } else if (component instanceof ExpansionCardEntity e) {
            this.expansionCards.add(e);
        } else if (component instanceof CaseFanEntity c) {
            this.caseFans.add(c);
        } else if (component instanceof OpticalDriveEntity o) {
            this.opticalDrives.add(o);
        } else if (component instanceof MonitorEntity m) {
            this.monitors.add(m);
        }
    }

    public List<MemoryEntity> requireMemories() {
        return require(memories, "Нет данных об оперативной памяти");
    }

    public List<CpuEntity> requireCpus() {
        return require(cpus, "Нет данных о процессорах");
    }

    public List<VideoCardEntity> requireGpus() {
        requireVideoCapability();
        return gpus;
    }

    public List<CpuCoolerEntity> requireCoolers() {
        return require(coolers, "Нет данных о кулерах");
    }

    public List<PowerSupplyEntity> requirePsus() {
        return require(psus, "Нет данных о блоках питания");
    }

    public Integer getTotalTdpW() {
        if (cpus.isEmpty()) {
            throw new MissingContextDataException("Нет данных о процессорах");
        }
        requireVideoCapability();
        int cpuTdp = cpus.stream().mapToInt(CpuEntity::getTdpW).sum();
        int gpuTdp = gpus.stream().mapToInt(VideoCardEntity::getTdpW).sum();
        return cpuTdp + gpuTdp;
    }

    public Set<MotherboardFormFactorEntity> requireCaseMoboFormFactors() {
        if (pcCase == null) {
            throw new MissingContextDataException("Нет данных о корпусе");
        }
        if (CollectionUtils.isEmpty(pcCase.getMoboFormFactors())) {
            throw new MissingContextDataException("Нет данных о поддерживаемых форм-факторах материнских плат корпусом");
        }
        return pcCase.getMoboFormFactors();
    }

    public List<Integer> requireCaseFanSizes() {
        if (pcCase == null) {
            throw new MissingContextDataException("Нет данных о корпусе");
        }
        if (CollectionUtils.isEmpty(pcCase.getFanSizes())) {
            throw new MissingContextDataException("Нет данных о поддерживаемых размерах корпусных вентиляторов");
        }
        return pcCase.getFanSizes();
    }

    public List<Integer> requireCaseRadiatorSizes() {
        if (pcCase == null) {
            throw new MissingContextDataException("Нет данных о корпусе");
        }
        if (CollectionUtils.isEmpty(pcCase.getRadiatorSizes())) {
            throw new MissingContextDataException("Нет данных о поддерживаемых размерах радиаторов корпусом");
        }
        return pcCase.getRadiatorSizes();
    }

    public Set<CpuSocketEntity> requireCoolerSockets(CpuCoolerEntity cooler) {
        if (cooler == null) {
            throw new MissingContextDataException("Нет данных о кулере");
        }
        if (CollectionUtils.isEmpty(cooler.getSockets())) {
            throw new MissingContextDataException("Нет данных о поддерживаемых сокетах кулера");
        }
        return cooler.getSockets();
    }

    public Integer getTotalPsuWattage() {
        if (psus.isEmpty()) {
            throw new MissingContextDataException("Нет данных о блоках питания");
        }
        return psus.stream().mapToInt(PowerSupplyEntity::getWattageW).sum();
    }

    public Integer getTotalRamCapacityGb() {
        if (memories.isEmpty()) {
            throw new MissingContextDataException("Нет данных об оперативной памяти");
        }
        return memories.stream().mapToInt(m -> m.getModulesCount() * m.getModulesSizeGb()).sum();
    }

    public Integer getTotalRamModules() {
        if (memories.isEmpty()) {
            throw new MissingContextDataException("Нет данных об оперативной памяти");
        }
        return memories.stream().mapToInt(MemoryEntity::getModulesCount).sum();
    }

    public Boolean canVerifyRegisteredMemorySupport() {
        for (MemoryEntity memory : memories) {
            if (memory.getIsRegistered() == null) {
                throw new MissingContextDataException("Нет данных о registered-признаке оперативной памяти");
            }
            if (memory.getIsRegistered()) {
                throw new MissingContextDataException("В модели материнской платы отсутствуют данные о поддержке Registered/RDIMM памяти");
            }
        }
        return true;
    }

    public Boolean canPlaceM2Storages() {
        List<Integer> requiredSizes = getRequiredM2StorageSizes();
        if (requiredSizes.isEmpty()) {
            return true;
        }

        List<M2Slot> storageSlots = getMotherboardM2StorageSlots();
        if (storageSlots.isEmpty()) {
            return false;
        }

        boolean[] used = new boolean[storageSlots.size()];
        return canAssignM2Storage(requiredSizes, storageSlots, used, 0);
    }

    public Boolean canPlaceM2ExpansionCards() {
        int requiredCount = getM2ExpansionCardCount();
        if (requiredCount == 0) {
            return true;
        }

        if (motherboard == null) {
            throw new MissingContextDataException("Нет данных о материнской плате");
        }
        if (motherboard.getM2Slots() == null) {
            throw new MissingContextDataException("Нет данных о слотах M.2 материнской платы");
        }

        long availableEKeySlots = motherboard.getM2Slots().stream()
                .filter(slot -> {
                    if (slot.keys() == null) {
                        throw new MissingContextDataException("Нет данных о ключе слота M.2 материнской платы");
                    }

                    return slot.keys().toLowerCase().contains("e-key");
                })
                .count();

        return requiredCount <= availableEKeySlots;
    }

    public Integer getM2ExpansionCardCount() {
        int count = 0;
        for (ExpansionCardEntity card : expansionCards) {
            String interfaceName = requireExpansionCardInterfaceName(card).toLowerCase();

            if (interfaceName.equals("m.2") || interfaceName.equals("m.2 pcie")) {
                count++;
            }
        }
        return count;
    }

    public Integer getStorageCountByFormFactor(String ffName) {
        int count = 0;
        for (StorageEntity storage : storages) {
            if (Boolean.TRUE.equals(storage.getIsExternal())) {
                continue;
            }

            String formFactorName = requireStorageFormFactorName(storage);

            if (formFactorName.contains(ffName)) {
                count++;
            }
        }
        return count;
    }

    public Boolean canConnectAllMonitorsToGpus() {
        if (monitors.isEmpty()) {
            return true;
        }
        if (gpus.isEmpty()) {
            throw new MissingContextDataException("Нет данных о видеовыходах: в сборке отсутствует дискретная видеокарта, а видеовыходы материнской платы не описаны");
        }

        Map<String, Integer> availableOutputs = getAvailableVideoOutputFamilies();
        if (availableOutputs.values().stream().mapToInt(Integer::intValue).sum() == 0) {
            throw new MissingContextDataException("Нет данных о видеовыходах видеокарт");
        }

        List<List<String>> requiredMonitorInputFamilies = getRequiredMonitorInputFamilies();
        boolean[] connected = new boolean[requiredMonitorInputFamilies.size()];

        return canAssignMonitorToOutput(requiredMonitorInputFamilies, availableOutputs, connected, 0);
    }

    public Integer getSataDevicesCount() {
        int count = 0;

        for (StorageEntity storage : storages) {
            if (Boolean.TRUE.equals(storage.getIsExternal())) {
                continue;
            }
            if (CollectionUtils.isEmpty(storage.getInterfaces())) {
                throw new MissingContextDataException("Нет данных об интерфейсах накопителя");
            }

            boolean usesInternalSataPort = storage.getInterfaces().stream()
                    .map(StorageInterfaceEntity::getName)
                    .anyMatch(this::isInternalSataPortInterface);

            if (usesInternalSataPort) {
                count++;
            }
        }

        for (OpticalDriveEntity drive : opticalDrives) {
            if (drive.getStorageInterface() == null || drive.getStorageInterface().getName() == null) {
                throw new MissingContextDataException("Нет данных об интерфейсе оптического привода");
            }

            if (isInternalSataPortInterface(drive.getStorageInterface().getName())) {
                count++;
            }
        }

        return count;
    }

    public Integer getMiniPcieMsataDeviceCount() {
        return getMsataStorageCount() + getMiniPcieExpansionCardCount();
    }

    public Integer getMiniPcieExpansionCardCount() {
        int count = 0;
        for (ExpansionCardEntity card : expansionCards) {
            String interfaceName = requireExpansionCardInterfaceName(card).toLowerCase();

            if (interfaceName.contains("mini-pcie")) {
                count++;
            }
        }
        return count;
    }

    public Integer getMsataStorageCount() {
        int count = 0;
        for (StorageEntity storage : storages) {
            if (Boolean.TRUE.equals(storage.getIsExternal())) {
                continue;
            }

            String formFactorName = requireStorageFormFactorName(storage);

            if (formFactorName.equalsIgnoreCase("mSATA")) {
                count++;
            }
        }
        return count;
    }

    public Boolean canPlacePcieDevices() {
        List<Integer> requiredWidths = getRequiredPcieSlotWidths();
        if (requiredWidths.isEmpty()) {
            return true;
        }

        List<Integer> availableWidths = getAvailablePcieSlotWidths();
        if (availableWidths.isEmpty()) {
            return false;
        }

        requiredWidths.sort(Comparator.reverseOrder());
        availableWidths.sort(Comparator.reverseOrder());

        boolean[] used = new boolean[availableWidths.size()];
        return canAssignPcieDevice(requiredWidths, availableWidths, used, 0);
    }

    public Integer getRegularPciDeviceCount() {
        int count = 0;
        for (ExpansionCardEntity card : expansionCards) {
            String interfaceName = requireExpansionCardInterfaceName(card);
            if (interfaceName.equalsIgnoreCase("PCI")) {
                count++;
            }
        }

        for (VideoCardEntity gpu : gpus) {
            String interfaceName = requireGpuInterfaceName(gpu);
            if (interfaceName.equalsIgnoreCase("PCI")) {
                count++;
            }
        }
        return count;
    }

    public Integer getTotalGpuCaseExpansionWidth() {
        int total = 0;
        if (gpus.isEmpty()) {
            requireVideoCapability();
            return total;
        }
        for (VideoCardEntity gpu : gpus) {
            if (gpu.getCaseExpansionWidth() == null) {
                throw new MissingContextDataException("Нет данных о ширине видеокарты по слотам корпуса");
            }
            total += gpu.getCaseExpansionWidth();
        }
        return total;
    }

    public Boolean canVerifyLegacyGpuInterfaces() {
        for (VideoCardEntity gpu : gpus) {
            String interfaceName = requireGpuInterfaceName(gpu);

            if (interfaceName.equalsIgnoreCase("AGP")) {
                throw new MissingContextDataException("В модели материнской платы отсутствуют данные об AGP-слотах");
            }
        }
        return true;
    }

    public Boolean isGcHpwrGpuCompatibleWithMotherboard() {
        boolean hasGcHpwrGpu = gpus.stream()
                .map(this::requireGpuInterfaceName)
                .anyMatch(interfaceName -> interfaceName.toLowerCase().contains("gc-hpwr"));

        if (!hasGcHpwrGpu) {
            return true;
        }

        if (motherboard == null) {
            throw new MissingContextDataException("Нет данных о материнской плате");
        }
        return motherboard.getUsesBackConnect();
    }

    public Boolean isEccSupported() {
        if (motherboard == null) {
            throw new MissingContextDataException("Нет данных о материнской плате");
        }
        if (cpus.isEmpty()) {
            throw new MissingContextDataException("Нет данных о процессорах");
        }

        if (Boolean.FALSE.equals(motherboard.getEccSupport())) {
            return false;
        }
        return cpus.stream()
                .allMatch(cpu -> Boolean.TRUE.equals(cpu.getEccSupport()));
    }

    public Integer getTotalSataPowerConnectors() {
        return sumPsuPowerPins(PowerSupplyEntity::getSataConnectors);
    }

    public Integer getFullSizeOpticalDriveCount() {
        int count = 0;
        for (OpticalDriveEntity drive : opticalDrives) {
            String formFactorName = requireOpticalDriveFormFactorName(drive);
            if (formFactorName.equals("5.25")) {
                count++;
            }
        }
        return count;
    }

    public Boolean canVerifySlimOpticalDrivePlacement() {
        for (OpticalDriveEntity drive : opticalDrives) {
            String formFactorName = requireOpticalDriveFormFactorName(drive).toLowerCase();
            if (formFactorName.contains("slim")) {
                throw new MissingContextDataException("Нет данных о поддержке slim-отсеков для оптических приводов корпусом");
            }
        }
        return true;
    }

    public Boolean canVerifyOpticalDriveInterfaces() {
        for (OpticalDriveEntity drive : opticalDrives) {
            String interfaceName = requireOpticalDriveInterfaceName(drive).toLowerCase();
            if (interfaceName.contains("pata")) {
                throw new MissingContextDataException("В модели материнской платы отсутствуют данные о PATA/IDE-портах для оптических приводов");
            }
        }
        return true;
    }

    public Boolean canPowerGpuPcie6And8PinConnectors() {
        int req8 = getReqPcie8Pin();
        int req6 = getReqPcie6Pin();

        int avail8 = sumPsuPowerPins(PowerSupplyEntity::getPcie8PinConnectors);
        int avail6 = sumPsuPowerPins(PowerSupplyEntity::getPcie6PinConnectors);
        int avail6Plus2 = sumPsuPowerPins(PowerSupplyEntity::getPcie6Plus2PinConnectors);

        int flexUsedFor8 = Math.max(0, req8 - avail8);
        int flexUsedFor6 = Math.max(0, req6 - avail6);

        return flexUsedFor8 + flexUsedFor6 <= avail6Plus2;
    }

    public Integer getReqPcie8Pin() {
        return sumGpuPowerPins(VideoCardEntity::getPower8pinCount);
    }

    public Integer getReqPcie6Pin() {
        return sumGpuPowerPins(VideoCardEntity::getPower6pinCount);
    }

    public Integer getReqPcie12vhpwr() {
        return sumGpuPowerPins(VideoCardEntity::getPower12vhpwrCount);
    }

    public Integer getReqPcie12Pin() {
        return sumGpuPowerPins(VideoCardEntity::getPower12pinCount);
    }

    public Integer getReqEps8Pin() {
        return sumGpuPowerPins(VideoCardEntity::getPowerEpsCount);
    }

    public Integer getAvailPcie8Pin() {
        return sumPsuPowerPins(psu ->
                psu.getPcie8PinConnectors() + psu.getPcie6Plus2PinConnectors()
        );
    }

    public Integer getAvailPcie6Pin() {
        return sumPsuPowerPins(psu ->
                psu.getPcie6PinConnectors() + psu.getPcie6Plus2PinConnectors()
        );
    }

    public Integer getAvailPcie12vhpwr() {
        return sumPsuPowerPins(PowerSupplyEntity::getPcie12vhpwrConnectors);
    }

    public Integer getAvailPcie12Pin() {
        return sumPsuPowerPins(PowerSupplyEntity::getPcie12PinConnectors);
    }

    public Integer getAvailEps8Pin() {
        return sumPsuPowerPins(PowerSupplyEntity::getEps8PinConnectors);
    }

    public Integer getRequiredFrontUsb20Headers() {
        return getRequiredFrontUsbHeadersByName("USB 2.0");
    }

    public Integer getRequiredFrontUsb32Gen1Headers() {
        return getRequiredFrontUsbHeadersByName("USB 3.2 Gen 1");
    }

    public Integer getRequiredFrontUsb32Gen2Headers() {
        return getRequiredFrontUsbHeadersByName("USB 3.2 Gen 2 Type-C");
    }

    public Integer getRequiredFrontUsb32Gen2x2Headers() {
        return getRequiredFrontUsbHeadersByName("USB 3.2 Gen 2x2");
    }

    private void requireVideoCapability() {
        if (gpus.isEmpty()) {
            if (cpus.isEmpty()) {
                throw new MissingContextDataException("Нет данных о процессорах и видеокартах");
            }
            if (cpus.stream().noneMatch(c -> c.getGraphics() != null)) {
                throw new MissingContextDataException("В сборке нет дискретной видеокарты и процессора со встроенным видеоядром");
            }
        }
    }

    private Integer sumGpuPowerPins(ToIntFunction<VideoCardEntity> mapper) {
        if (gpus.isEmpty()) {
            requireVideoCapability();
            return 0;
        }
        return gpus.stream().mapToInt(mapper).sum();
    }

    private Integer sumPsuPowerPins(ToIntFunction<PowerSupplyEntity> mapper) {
        if (psus.isEmpty()) {
            throw new MissingContextDataException("Нет данных о блоках питания");
        }
        return psus.stream().mapToInt(mapper).sum();
    }

    private boolean isInternalSataPortInterface(String interfaceName) {
        if (interfaceName == null) {
            throw new MissingContextDataException("Нет данных об интерфейсе накопителя");
        }

        String normalized = interfaceName.toLowerCase();

        return normalized.startsWith("sata ");
    }

    private Integer getRequiredFrontUsbHeadersByName(String usbNamePart) {
        if (pcCase == null) {
            throw new MissingContextDataException("Нет данных о корпусе");
        }
        if (CollectionUtils.isEmpty(pcCase.getFrontPanelUsbTypes())) {
            throw new MissingContextDataException("Нет данных о разъемах USB на передней панели корпуса");
        }
        return (int) pcCase.getFrontPanelUsbTypes().stream()
                .filter(usb -> usb.getName() != null && usb.getName().contains(usbNamePart))
                .count();
    }

    private List<Integer> getRequiredM2StorageSizes() {
        List<Integer> result = new ArrayList<>();

        for (StorageEntity storage : storages) {
            if (Boolean.TRUE.equals(storage.getIsExternal())) {
                continue;
            }

            String formFactorName = requireStorageFormFactorName(storage);

            Matcher matcher = M2_FORM_FACTOR_PATTERN.matcher(formFactorName);
            if (matcher.find()) {
                result.add(Integer.parseInt(matcher.group(1)));
            }
        }

        return result;
    }

    private List<M2Slot> getMotherboardM2StorageSlots() {
        if (motherboard == null) {
            throw new MissingContextDataException("Нет данных о материнской плате");
        }
        if (motherboard.getM2Slots() == null) {
            throw new MissingContextDataException("Нет данных о слотах M.2 материнской платы");
        }

        return motherboard.getM2Slots().stream()
                .filter(slot -> {
                    if (slot.keys() == null) {
                        throw new MissingContextDataException("Нет данных о ключе слота M.2 материнской платы");
                    }
                    String keys = slot.keys().toLowerCase();
                    return keys.contains("m-key") || keys.contains("b-key");
                })
                .toList();
    }

    private boolean canAssignM2Storage(List<Integer> requiredSizes,
                                       List<M2Slot> slots,
                                       boolean[] used,
                                       int storageIndex) {
        if (storageIndex >= requiredSizes.size()) {
            return true;
        }

        Integer requiredSize = requiredSizes.get(storageIndex);

        for (int i = 0; i < slots.size(); i++) {
            if (used[i]) {
                continue;
            }

            if (getM2SlotSizes(slots.get(i)).contains(requiredSize)) {
                used[i] = true;

                if (canAssignM2Storage(requiredSizes, slots, used, storageIndex + 1)) {
                    return true;
                }

                used[i] = false;
            }
        }

        return false;
    }

    private List<Integer> getM2SlotSizes(M2Slot slot) {
        if (slot == null || CollectionUtils.isEmpty(slot.sizes())) {
            throw new MissingContextDataException("Нет данных о поддерживаемых типоразмерах слота M.2");
        }

        return slot.sizes();
    }

    private List<Integer> getRequiredPcieSlotWidths() {
        List<Integer> result = new ArrayList<>();

        for (VideoCardEntity gpu : gpus) {
            String interfaceName = requireGpuInterfaceName(gpu);
            Integer width = parsePcieWidth(interfaceName);

            if (width != null) {
                result.add(width);
            }
        }

        for (ExpansionCardEntity card : expansionCards) {
            String interfaceName = requireExpansionCardInterfaceName(card);
            Integer width = parsePcieWidth(interfaceName);

            if (width != null) {
                result.add(width);
            }
        }

        for (StorageEntity storage : storages) {
            if (Boolean.TRUE.equals(storage.getIsExternal())) {
                continue;
            }

            String formFactorName = requireStorageFormFactorName(storage);
            if (!formFactorName.equalsIgnoreCase("PCIe")) {
                continue;
            }

            Integer width = getPcieStorageWidth(storage);
            result.add(width);
        }

        return result;
    }

    private List<Integer> getAvailablePcieSlotWidths() {
        if (motherboard == null) {
            throw new MissingContextDataException("Нет данных о материнской плате");
        }

        List<Integer> result = new ArrayList<>();

        addRepeated(result, 16, motherboard.getPciX16Slots());
        addRepeated(result, 8, motherboard.getPciX8Slots());
        addRepeated(result, 4, motherboard.getPciX4Slots());
        addRepeated(result, 1, motherboard.getPciX1Slots());

        return result;
    }

    private void addRepeated(List<Integer> target, int width, Integer count) {
        if (count == null) {
            throw new MissingContextDataException("Нет данных о количестве PCIe x" + width + " слотов");
        }

        for (int i = 0; i < count; i++) {
            target.add(width);
        }
    }

    private boolean canAssignPcieDevice(List<Integer> requiredWidths,
                                        List<Integer> availableWidths,
                                        boolean[] used,
                                        int deviceIndex) {
        if (deviceIndex >= requiredWidths.size()) {
            return true;
        }

        Integer requiredWidth = requiredWidths.get(deviceIndex);

        for (int i = 0; i < availableWidths.size(); i++) {
            if (used[i]) {
                continue;
            }

            if (availableWidths.get(i) >= requiredWidth) {
                used[i] = true;

                if (canAssignPcieDevice(requiredWidths, availableWidths, used, deviceIndex + 1)) {
                    return true;
                }

                used[i] = false;
            }
        }

        return false;
    }

    private Integer getPcieStorageWidth(StorageEntity storage) {
        if (CollectionUtils.isEmpty(storage.getInterfaces())) {
            throw new MissingContextDataException("Нет данных об интерфейсах PCIe-накопителя");
        }

        return storage.getInterfaces().stream()
                .map(StorageInterfaceEntity::getName)
                .map(this::parsePcieWidth)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new MissingContextDataException("Нет данных о ширине PCIe-интерфейса накопителя"));
    }

    private Integer parsePcieWidth(String interfaceName) {
        if (interfaceName == null) {
            throw new MissingContextDataException("Нет данных об интерфейсе PCIe-устройства");
        }

        Matcher matcher = PCIE_INTERFACE_PATTERN.matcher(interfaceName.trim());

        if (!matcher.matches()) {
            return null;
        }

        return Integer.parseInt(matcher.group(1));
    }

    private Map<String, Integer> getAvailableVideoOutputFamilies() {
        Map<String, Integer> result = new HashMap<>();

        for (VideoCardEntity gpu : gpus) {
            if (gpu.getVideoOutputs() == null || gpu.getVideoOutputs().isEmpty()) {
                throw new MissingContextDataException("Нет данных о видеовыходах видеокарты");
            }

            for (Map.Entry<String, Integer> entry : gpu.getVideoOutputs().entrySet()) {
                String family = normalizeVideoOutputFamily(entry.getKey());

                if (family == null) {
                    continue;
                }

                Integer count = entry.getValue();

                if (count == null) {
                    throw new MissingContextDataException("Нет данных о количестве видеовыходов видеокарты");
                }

                result.merge(family, count, Integer::sum);
            }
        }

        return result;
    }

    private List<List<String>> getRequiredMonitorInputFamilies() {
        List<List<String>> result = new ArrayList<>();

        for (MonitorEntity monitor : monitors) {
            List<String> families = getMonitorInputFamilies(monitor);
            if (families.isEmpty()) {
                throw new MissingContextDataException("Нет данных о видеовходах монитора");
            }
            result.add(families);
        }

        return result;
    }

    private List<String> getMonitorInputFamilies(MonitorEntity monitor) {
        if (monitor == null) {
            throw new MissingContextDataException("Нет данных о мониторе");
        }

        List<String> result = new ArrayList<>();

        addMonitorInputFamily(result, "HDMI", monitor.getInputHdmi());
        addMonitorInputFamily(result, "HDMI", monitor.getInputMiniHdmi());
        addMonitorInputFamily(result, "HDMI", monitor.getInputMicroHdmi());

        addMonitorInputFamily(result, "DISPLAYPORT", monitor.getInputDp());
        addMonitorInputFamily(result, "DISPLAYPORT", monitor.getInputMiniDp());

        addMonitorInputFamily(result, "DVI", monitor.getInputDvi());
        addMonitorInputFamily(result, "VGA", monitor.getInputVga());
        addMonitorInputFamily(result, "USB_C", monitor.getInputUsbC());
        addMonitorInputFamily(result, "BNC", monitor.getInputBnc());
        addMonitorInputFamily(result, "COMPONENT", monitor.getInputComponent());
        addMonitorInputFamily(result, "S_VIDEO", monitor.getInputSVideo());
        addMonitorInputFamily(result, "VIRTUAL_LINK", monitor.getInputVirtualLink());

        return result.stream().distinct().toList();
    }

    private void addMonitorInputFamily(List<String> result, String family, Integer count) {
        if (count == null) {
            throw new MissingContextDataException("Нет данных о количестве видеовходов монитора");
        }
        if (count > 0) {
            result.add(family);
        }
    }

    private boolean canAssignMonitorToOutput(List<List<String>> monitorInputFamilies,
                                             Map<String, Integer> availableOutputs,
                                             boolean[] connected,
                                             int monitorIndex) {
        if (monitorIndex >= monitorInputFamilies.size()) {
            return true;
        }

        List<String> possibleFamilies = monitorInputFamilies.get(monitorIndex);

        for (String family : possibleFamilies) {
            int availableCount = availableOutputs.getOrDefault(family, 0);
            if (availableCount <= 0) {
                continue;
            }

            availableOutputs.put(family, availableCount - 1);
            connected[monitorIndex] = true;

            if (canAssignMonitorToOutput(monitorInputFamilies, availableOutputs, connected, monitorIndex + 1)) {
                return true;
            }

            connected[monitorIndex] = false;
            availableOutputs.put(family, availableCount);
        }

        return false;
    }

    private String normalizeVideoOutputFamily(String outputName) {
        if (outputName == null) {
            throw new MissingContextDataException("Нет данных о типе видеовыхода видеокарты");
        }

        String normalized = outputName.toLowerCase();

        if (normalized.contains("hdmi")) {
            return "HDMI";
        }

        if (normalized.contains("displayport") || normalized.contains("minidisplayport")) {
            return "DISPLAYPORT";
        }

        if (normalized.contains("dvi")) {
            return "DVI";
        }

        if (normalized.contains("vga")) {
            return "VGA";
        }

        if (normalized.contains("usb_type_c")) {
            return "USB_C";
        }

        if (normalized.contains("bnc")) {
            return "BNC";
        }

        if (normalized.contains("component")) {
            return "COMPONENT";
        }

        if (normalized.contains("s_video")) {
            return "S_VIDEO";
        }

        if (normalized.contains("virtuallink")) {
            return "VIRTUAL_LINK";
        }

        return null;
    }

    private String requireGpuInterfaceName(VideoCardEntity gpu) {
        if (gpu == null || gpu.getInterfaceType() == null || gpu.getInterfaceType().getName() == null) {
            throw new MissingContextDataException("Нет данных об интерфейсе видеокарты");
        }

        return gpu.getInterfaceType().getName();
    }

    private String requireExpansionCardInterfaceName(ExpansionCardEntity card) {
        if (card == null || card.getInterfaceType() == null || card.getInterfaceType().getName() == null) {
            throw new MissingContextDataException("Нет данных об интерфейсе карты расширения");
        }

        return card.getInterfaceType().getName();
    }

    private String requireStorageFormFactorName(StorageEntity storage) {
        if (storage == null || storage.getFormFactor() == null || storage.getFormFactor().getName() == null) {
            throw new MissingContextDataException("Нет данных о форм-факторе накопителя");
        }

        return storage.getFormFactor().getName();
    }

    private String requireOpticalDriveFormFactorName(OpticalDriveEntity drive) {
        if (drive == null || drive.getFormFactor() == null || drive.getFormFactor().getName() == null) {
            throw new MissingContextDataException("Нет данных о форм-факторе оптического привода");
        }

        return drive.getFormFactor().getName();
    }

    private String requireOpticalDriveInterfaceName(OpticalDriveEntity drive) {
        if (drive == null || drive.getStorageInterface() == null || drive.getStorageInterface().getName() == null) {
            throw new MissingContextDataException("Нет данных об интерфейсе оптического привода");
        }

        return drive.getStorageInterface().getName();
    }

    private <T> List<T> require(List<T> list, String message) {
        if (list == null || list.isEmpty()) {
            throw new MissingContextDataException(message);
        }
        return list;
    }
}
