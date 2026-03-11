package com.github.randdd32.donor_search_backend.web.mapper.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.*;
import com.github.randdd32.donor_search_backend.web.dto.dictionary.NamedDictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DictionaryMapper {
    NamedDictionaryDto toDto(ManufacturerEntity entity);
    NamedDictionaryDto toDto(StorageInterfaceEntity entity);
    NamedDictionaryDto toDto(OpticalDriveFormFactorEntity entity);
    NamedDictionaryDto toDto(GpuChipsetEntity entity);
    NamedDictionaryDto toDto(MemoryTypeEntity entity);
    NamedDictionaryDto toDto(ExpansionInterfaceEntity entity);
    NamedDictionaryDto toDto(ColorEntity entity);
    NamedDictionaryDto toDto(CaseTypeEntity entity);
    NamedDictionaryDto toDto(FrontPanelUsbEntity entity);
    NamedDictionaryDto toDto(MotherboardFormFactorEntity entity);
    NamedDictionaryDto toDto(SidePanelEntity entity);
    NamedDictionaryDto toDto(CpuSocketEntity entity);
    NamedDictionaryDto toDto(PowerSupplyTypeEntity entity);
    NamedDictionaryDto toDto(EfficiencyRatingEntity entity);
    NamedDictionaryDto toDto(ModularTypeEntity entity);
    NamedDictionaryDto toDto(FanConnectorEntity entity);
    NamedDictionaryDto toDto(MicroarchitectureEntity entity);
    NamedDictionaryDto toDto(IntegratedGraphicsEntity entity);
    NamedDictionaryDto toDto(StorageTypeEntity entity);
    NamedDictionaryDto toDto(StorageFormFactorEntity entity);
    NamedDictionaryDto toDto(RamFormFactorEntity entity);
    NamedDictionaryDto toDto(MonitorResolutionEntity entity);
    NamedDictionaryDto toDto(PanelTypeEntity entity);
    NamedDictionaryDto toDto(AspectRatioEntity entity);
}
