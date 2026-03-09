package com.github.randdd32.donor_search_backend.web.mapper.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.CaseTypeEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.ColorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.CpuSocketEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.ExpansionInterfaceEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.FrontPanelUsbEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.GpuChipsetEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.ManufacturerEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.MemoryTypeEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.MotherboardFormFactorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.OpticalDriveFormFactorEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.SidePanelEntity;
import com.github.randdd32.donor_search_backend.model.dictionary.StorageInterfaceEntity;
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
}
