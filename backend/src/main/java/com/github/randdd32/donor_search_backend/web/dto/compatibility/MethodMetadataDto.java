package com.github.randdd32.donor_search_backend.web.dto.compatibility;

public record MethodMetadataDto(
        String methodSignature,
        String returnType,
        String description,
        Boolean isNullable
) {}
