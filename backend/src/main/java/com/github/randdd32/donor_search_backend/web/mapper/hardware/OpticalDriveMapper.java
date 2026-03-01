package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.OpticalDriveEntity;
import com.github.randdd32.donor_search_backend.model.hardware.OpticalDrivePartNumberEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.OpticalDriveDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OpticalDriveMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "formFactorName", source = "formFactor.name")
    @Mapping(target = "interfaceName", source = "storageInterface.name")
    OpticalDriveDto toDto(OpticalDriveEntity entity);

    default List<String> mapPartNumbers(Set<OpticalDrivePartNumberEntity> partNumbers) {
        if (partNumbers == null || partNumbers.isEmpty()) {
            return Collections.emptyList();
        }
        return partNumbers.stream()
                .map(OpticalDrivePartNumberEntity::getPartNumber)
                .collect(Collectors.toList());
    }
}
