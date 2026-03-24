package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.StorageEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.StorageDto;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryNameMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {
        BaseHardwareMapper.class,
        DictionaryNameMapper.class
})
public interface StorageMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "typeName", source = "storageType.name")
    @Mapping(target = "formFactorName", source = "formFactor.name")
    @Mapping(target = "colorName", source = "color.name")
    StorageDto toDto(StorageEntity entity);
}
