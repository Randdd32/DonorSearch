package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.CpuEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.CpuDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BaseHardwareMapper.class)
public interface CpuMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "socketName", source = "socket.name")
    @Mapping(target = "microarchitectureName", source = "microarchitecture.name")
    @Mapping(target = "graphicsName", source = "graphics.name")
    CpuDto toDto(CpuEntity entity);
}
