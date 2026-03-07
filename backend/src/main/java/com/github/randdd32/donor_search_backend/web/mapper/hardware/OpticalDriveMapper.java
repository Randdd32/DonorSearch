package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.OpticalDriveDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BaseHardwareMapper.class)
public interface OpticalDriveMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "formFactorName", source = "formFactor.name")
    @Mapping(target = "interfaceName", source = "storageInterface.name")
    OpticalDriveDto toDto(OpticalDriveEntity entity);
}
