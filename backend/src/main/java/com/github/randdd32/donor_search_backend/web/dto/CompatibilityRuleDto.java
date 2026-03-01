package com.github.randdd32.donor_search_backend.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompatibilityRuleDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 100)
    private String ruleCode;

    @NotBlank
    private String expression;

    @NotBlank
    private String errorMessage;

    @NotNull
    private Boolean isActive;

    private String description;
}
