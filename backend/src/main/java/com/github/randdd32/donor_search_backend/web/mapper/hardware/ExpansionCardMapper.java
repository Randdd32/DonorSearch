package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.ExpansionCardEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.ExpansionCardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = BaseHardwareMapper.class)
public interface ExpansionCardMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "interfaceName", source = "interfaceType.name")
    @Mapping(target = "colorName", source = "color.name")
    @Mapping(target = "audioChipsetName", source = "audioChipset.name")
    @Mapping(target = "protocolName", source = "protocol.name")
    ExpansionCardDto toDto(ExpansionCardEntity entity);
}
