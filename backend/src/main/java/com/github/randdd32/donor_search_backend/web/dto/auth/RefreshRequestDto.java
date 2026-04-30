package com.github.randdd32.donor_search_backend.web.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshRequestDto(
        @NotBlank
        @Size(max = 200)
        String fingerprint
) {}
