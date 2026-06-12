package com.github.randdd32.donor_search_backend.web.mapper;

import com.github.randdd32.donor_search_backend.model.IntegrationMappingEntity;
import com.github.randdd32.donor_search_backend.web.dto.IntegrationMappingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IntegrationMappingMapper {
    @Mapping(target = "internalComponentId", source = "internalComponent.id")
    @Mapping(target = "internalComponentName", source = "internalComponent.name")
    @Mapping(target = "internalComponentType", source = "internalComponent.type")
    @Mapping(target = "internalComponentSearchName", source = "internalComponent.searchName")
    IntegrationMappingDto toDto(IntegrationMappingEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "internalComponent", ignore = true)
    IntegrationMappingEntity toEntity(IntegrationMappingDto dto);
}
