package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.PowerSupplyEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.PowerSupplyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BaseHardwareMapper.class)
public interface PowerSupplyMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "typeName", source = "powerSupplyType.name")
    @Mapping(target = "efficiencyName", source = "efficiency.name")
    @Mapping(target = "modularName", source = "modular.name")
    @Mapping(target = "colorName", source = "color.name")
    PowerSupplyDto toDto(PowerSupplyEntity entity);
}
