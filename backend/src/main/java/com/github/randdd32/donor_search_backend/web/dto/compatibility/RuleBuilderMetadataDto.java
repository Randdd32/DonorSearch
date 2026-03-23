package com.github.randdd32.donor_search_backend.web.dto.compatibility;

import java.util.List;
import java.util.Map;

public record RuleBuilderMetadataDto(
        Map<String, String> contextProperties,
        List<MethodMetadataDto> contextMethods,
        Map<String, List<FieldMetadataDto>> componentFields
) {}
