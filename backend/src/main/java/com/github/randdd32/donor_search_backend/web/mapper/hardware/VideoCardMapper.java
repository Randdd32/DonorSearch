package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.VideoCardEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.VideoCardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BaseHardwareMapper.class)
public interface VideoCardMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "chipsetName", source = "chipset.name")
    @Mapping(target = "memoryTypeName", source = "memoryType.name")
    @Mapping(target = "interfaceName", source = "interfaceType.name")
    @Mapping(target = "colorName", source = "color.name")
    VideoCardDto toDto(VideoCardEntity entity);
}
