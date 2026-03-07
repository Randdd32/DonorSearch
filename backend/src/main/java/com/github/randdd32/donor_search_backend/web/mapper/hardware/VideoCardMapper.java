package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.VideoCardEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.VideoCardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VideoCardMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "chipsetName", source = "chipset.name")
    @Mapping(target = "memoryTypeName", source = "memoryType.name")
    @Mapping(target = "interfaceName", source = "interfaceType.name")
    @Mapping(target = "colorName", source = "color.name")
    VideoCardDto toDto(VideoCardEntity entity);

    default List<String> mapPartNumbers(Set<VideoCardPartNumberEntity> partNumbers) {
        if (CollectionUtils.isEmpty(partNumbers)) {
            return Collections.emptyList();
        }
        return partNumbers.stream()
                .map(VideoCardPartNumberEntity::getPartNumber)
                .collect(Collectors.toList());
    }
}
