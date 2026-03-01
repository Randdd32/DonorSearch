package com.github.randdd32.donor_search_backend.web.mapper;

import com.github.randdd32.donor_search_backend.model.CompatibilityRuleEntity;
import com.github.randdd32.donor_search_backend.web.dto.CompatibilityRuleDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompatibilityRuleMapper {
    CompatibilityRuleDto toDto(CompatibilityRuleEntity entity);
    CompatibilityRuleEntity toEntity(CompatibilityRuleDto dto);
}
