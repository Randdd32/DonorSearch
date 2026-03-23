package com.github.randdd32.donor_search_backend.web.dto.compatibility;

public record FieldMetadataDto(
        String fieldPath,
        String dataType,
        String description,
        Boolean isNullable
) {}
