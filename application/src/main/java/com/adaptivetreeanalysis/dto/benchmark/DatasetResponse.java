package com.adaptivetreeanalysis.dto.benchmark;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DatasetResponse(
        UUID id,
        String name,
        String distributionType,
        Integer size,
        Long seed,
        String payload,
        OffsetDateTime createdAt
) {
}
