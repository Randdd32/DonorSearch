package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.MonitorEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.MonitorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BaseHardwareMapper.class)
public interface MonitorMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "resolutionName", source = "resolution.name")
    @Mapping(target = "panelTypeName", source = "panelType.name")
    @Mapping(target = "aspectRatioName", source = "aspectRatio.name")
    MonitorDto toDto(MonitorEntity entity);
}
