package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.MotherboardEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.MotherboardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BaseHardwareMapper.class)
public interface MotherboardMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "socketName", source = "socket.name")
    @Mapping(target = "formFactorName", source = "formFactor.name")
    @Mapping(target = "memoryTypeName", source = "memoryType.name")
    @Mapping(target = "colorName", source = "color.name")
    MotherboardDto toDto(MotherboardEntity entity);
}
