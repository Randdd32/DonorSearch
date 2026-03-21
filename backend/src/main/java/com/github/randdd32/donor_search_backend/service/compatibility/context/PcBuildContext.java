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
}
