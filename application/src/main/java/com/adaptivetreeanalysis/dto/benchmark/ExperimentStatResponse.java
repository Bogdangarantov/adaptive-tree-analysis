package com.adaptivetreeanalysis.dto.benchmark;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ExperimentStatResponse(
        UUID id,
        UUID experimentId,
        String treeType,
        String algorithmVersion,
        Long executionTimeNs,
        Integer operationCount,
        Integer rotationCount,
        Integer treeHeight,
        Integer rebalancesCount,
        BigDecimal avgNodeDepth,
        Integer maxNodeDepth,
        Long memoryBytes,
        OffsetDateTime createdAt
) {
}
