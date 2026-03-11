package com.adaptivetreeanalysis.dto.benchmark;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ExperimentResponse(
        UUID id,
        UUID datasetId,
        String title,
        String operationsProfile,
        String status,
        String hardwareInfo,
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        OffsetDateTime createdAt
) {
}
