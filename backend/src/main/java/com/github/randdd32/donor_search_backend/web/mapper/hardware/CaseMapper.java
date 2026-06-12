package com.github.randdd32.donor_search_backend.web.mapper.hardware;

import com.github.randdd32.donor_search_backend.model.hardware.CaseEntity;
import com.github.randdd32.donor_search_backend.web.dto.hardware.CaseDto;
import com.github.randdd32.donor_search_backend.web.mapper.dictionary.DictionaryNameMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {
        BaseHardwareMapper.class,
        DictionaryNameMapper.class
})
public interface CaseMapper {
    @Mapping(target = "manufacturerName", source = "manufacturer.name")
    @Mapping(target = "caseTypeName", source = "caseType.name")
    @Mapping(target = "colorName", source = "color.name")
    @Mapping(target = "sidePanelName", source = "sidePanel.name")
    CaseDto toDto(CaseEntity entity);
}
