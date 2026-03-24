package com.github.randdd32.donor_search_backend.web.mapper.hardware;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComponentDtoMapperFacade {
    private final CaseMapper caseMapper;
    private final MotherboardMapper motherboardMapper;
    private final PowerSupplyMapper powerSupplyMapper;
    private final CpuMapper cpuMapper;
    private final CpuCoolerMapper cpuCoolerMapper;
    private final VideoCardMapper videoCardMapper;
    private final MemoryMapper memoryMapper;
    private final StorageMapper storageMapper;
    private final ExpansionCardMapper expansionCardMapper;
    private final CaseFanMapper caseFanMapper;
    private final OpticalDriveMapper opticalDriveMapper;
    private final MonitorMapper monitorMapper;

    /**
     * Превращает любую Entity комплектующего в соответствующий DTO.
     */
    public Object toDto(ComponentEntity entity) {
        if (entity == null) return null;

        return switch (entity.getType()) {
            case CASE -> caseMapper.toDto((CaseEntity) entity);
            case MOTHERBOARD -> motherboardMapper.toDto((MotherboardEntity) entity);
            case POWER_SUPPLY -> powerSupplyMapper.toDto((PowerSupplyEntity) entity);
            case CPU -> cpuMapper.toDto((CpuEntity) entity);
            case CPU_COOLER -> cpuCoolerMapper.toDto((CpuCoolerEntity) entity);
            case VIDEO_CARD -> videoCardMapper.toDto((VideoCardEntity) entity);
            case MEMORY -> memoryMapper.toDto((MemoryEntity) entity);
            case STORAGE -> storageMapper.toDto((StorageEntity) entity);
            case EXPANSION_CARD -> expansionCardMapper.toDto((ExpansionCardEntity) entity);
            case CASE_FAN -> caseFanMapper.toDto((CaseFanEntity) entity);
            case OPTICAL_DRIVE -> opticalDriveMapper.toDto((OpticalDriveEntity) entity);
            case MONITOR -> monitorMapper.toDto((MonitorEntity) entity);
        };
    }
}
