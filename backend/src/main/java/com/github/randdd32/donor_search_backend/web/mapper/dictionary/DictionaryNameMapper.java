package com.github.randdd32.donor_search_backend.web.mapper.dictionary;

import com.github.randdd32.donor_search_backend.model.dictionary.NamedDictionaryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DictionaryNameMapper {
    default String toName(NamedDictionaryEntity entity) {
        if (entity == null) {
            return null;
        }
        return entity.getName();
    }
}
