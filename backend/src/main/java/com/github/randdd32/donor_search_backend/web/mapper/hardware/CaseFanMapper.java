package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.CaseFanEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.CaseFanDto;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryNameMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {
        BaseHardwareMapper.class,
        DictionaryNameMapper.class
})
public interface CaseFanMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "colorName", source = "color.name")
    CaseFanDto toDto(CaseFanEntity entity);
}
