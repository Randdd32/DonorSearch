package com.github.randdd32.donor_search_backend.service.compatibility.context;

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
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

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
        if (memories.isEmpty()) {
            throw new IllegalStateException("Нет данных об оперативной памяти");
        }
        return memories;
    }

    public List<CpuEntity> requireCpus() {
        if (cpus.isEmpty()) {
            throw new IllegalStateException("Нет данных о процессорах");
        }
        return cpus;
    }

    public List<VideoCardEntity> requireGpus() {
        requireVideoCapability();
        return gpus;
    }

    public List<CpuCoolerEntity> requireCoolers() {
        if (coolers.isEmpty()) {
            throw new IllegalStateException("Нет данных о кулерах");
        }
        return coolers;
    }

    public Integer getTotalTdpW() {
        if (cpus.isEmpty()) {
            throw new IllegalStateException("В сборке отсутствует информация о процессорах");
        }
        int cpuTdp = cpus.stream().mapToInt(CpuEntity::getTdpW).sum();
        int gpuTdp = gpus.stream().mapToInt(VideoCardEntity::getTdpW).sum();
        return cpuTdp + gpuTdp;
    }

    public Integer getTotalPsuWattage() {
        if (psus.isEmpty()) {
            throw new IllegalStateException("В сборке отсутствует информация о блоках питания");
        }
        return psus.stream().mapToInt(PowerSupplyEntity::getWattageW).sum();
    }

    public Integer getTotalRamCapacityGb() {
        if (memories.isEmpty()) {
            throw new IllegalStateException("В сборке отсутствует информация об оперативной памяти");
        }
        return memories.stream().mapToInt(m -> m.getModulesCount() * m.getModulesSizeGb()).sum();
    }

    public Integer getTotalRamModules() {
        if (memories.isEmpty()) {
            throw new IllegalStateException("В сборке отсутствует информация об оперативной памяти");
        }
        return memories.stream().mapToInt(MemoryEntity::getModulesCount).sum();
    }

    public Integer getStorageCountByFormFactor(String ffName) {
        if (storages.isEmpty()) {
            throw new IllegalStateException("В сборке отсутствует информация об накопителях");
        }
        return (int) storages.stream()
                .filter(s -> s.getFormFactor() != null && s.getFormFactor().getName().contains(ffName))
                .count();
    }

    public Integer getSataDevicesCount() {
        if (storages.isEmpty()) {
            throw new IllegalStateException("В сборке отсутствует информация об накопителях");
        }

        long sataDisks = storages.stream()
                .filter(s -> s.getInterfaces() != null && s.getInterfaces().stream().anyMatch(i -> i.getName().toLowerCase().contains("sata")))
                .count();

        long sataOpticalDrives = opticalDrives.stream()
                .filter(o -> o.getStorageInterface() != null && o.getStorageInterface().getName().toLowerCase().contains("sata"))
                .count();

        return (int) (sataDisks + sataOpticalDrives);
    }

    public Integer getTotalGpuSlotWidth() {
        if (gpus.isEmpty()) {
            requireVideoCapability();
            return 0;
        }
        return gpus.stream().mapToInt(VideoCardEntity::getSlotWidth).sum();
    }

    public Boolean isEccSupported() {
        if (motherboard == null) {
            throw new IllegalStateException("В сборке отсутствует информация о материнской плате");
        }
        if (Boolean.FALSE.equals(motherboard.getEccSupport())) {
            return false;
        }
        if (cpus.isEmpty()) {
            throw new IllegalStateException("В сборке отсутствует информация о процессорах");
        }
        return cpus.stream()
                .allMatch(cpu -> Boolean.TRUE.equals(cpu.getEccSupport()));
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

    public Integer getAvailPcie8Pin() {
        return sumPsuPowerPins(PowerSupplyEntity::getPcie8PinConnectors);
    }

    public Integer getAvailPcie6Pin() {
        return sumPsuPowerPins(PowerSupplyEntity::getPcie6PinConnectors);
    }

    public Integer getAvailPcie12vhpwr() {
        return sumPsuPowerPins(PowerSupplyEntity::getPcie12vhpwrConnectors);
    }

    private void requireVideoCapability() {
        if (gpus.isEmpty()) {
            if (cpus.isEmpty()) {
                throw new IllegalStateException("В сборке отсутствует информация о процессорах и видеокартах");
            }
            if (cpus.stream().noneMatch(c -> c.getGraphics() != null)) {
                throw new IllegalStateException("В сборке нет дискретной видеокарты и процессора со встроенным видеоядром");
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
            throw new IllegalStateException("В сборке отсутствует информация о блоках питания");
        }
        return psus.stream().mapToInt(mapper).sum();
    }
}
