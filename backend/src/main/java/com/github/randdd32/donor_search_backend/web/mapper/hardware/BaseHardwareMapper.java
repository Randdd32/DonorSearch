package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.PartNumberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BaseHardwareMapper {
    default List<String> mapPartNumbers(Set<PartNumberEntity> partNumbers) {
        if (CollectionUtils.isEmpty(partNumbers)) {
            return Collections.emptyList();
        }
        return partNumbers.stream()
                .map(PartNumberEntity::getPartNumber)
                .collect(Collectors.toList());
    }
}
