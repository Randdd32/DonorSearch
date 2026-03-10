package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.CpuCoolerEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.CpuCoolerDto;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryNameMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {
        BaseHardwareMapper.class,
        DictionaryNameMapper.class
})
public interface CpuCoolerMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "colorName", source = "color.name")
    CpuCoolerDto toDto(CpuCoolerEntity entity);
}
