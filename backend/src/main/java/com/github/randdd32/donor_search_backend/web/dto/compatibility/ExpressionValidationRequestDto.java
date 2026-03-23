package com.github.randdd32.donor_search_backend.web.dto.compatibility;

import jakarta.validation.constraints.NotBlank;

public record ExpressionValidationRequestDto(
        @NotBlank(message = "Expression must not be empty")
        String expression
) {}
