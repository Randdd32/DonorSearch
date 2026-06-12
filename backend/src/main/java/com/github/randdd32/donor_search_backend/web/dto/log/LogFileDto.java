package com.github.randdd32.donor_search_backend.web.dto.log;

import java.time.Instant;

public record LogFileDto(
        String filename,
        long sizeBytes,
        Instant lastModified
) {}
