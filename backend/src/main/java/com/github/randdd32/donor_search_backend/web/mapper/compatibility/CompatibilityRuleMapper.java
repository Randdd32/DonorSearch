package com.github.randdd32.donor_search_backend.web.mapper.compatibility;

import com.github.randdd32.donor_search_backend.model.compatibility.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.web.dto.compatibility.CompatibilityRuleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompatibilityRuleMapper {
    CompatibilityRuleDto toDto(CompatibilityRuleEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CompatibilityRuleEntity toEntity(CompatibilityRuleDto dto);
}
