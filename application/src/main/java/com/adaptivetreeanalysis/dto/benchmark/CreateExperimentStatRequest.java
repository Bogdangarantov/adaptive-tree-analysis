package com.adaptivetreeanalysis.dto.benchmark;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateExperimentStatRequest(
        @NotBlank String treeType,
        String algorithmVersion,
        @NotNull @Min(0) Long executionTimeNs,
        @NotNull @Min(0) Integer operationCount,
        @NotNull @Min(0) Integer rotationCount,
        @NotNull @Min(0) Integer treeHeight,
        @Min(0) Integer rebalancesCount,
        @Min(0) BigDecimal avgNodeDepth,
        @Min(0) Integer maxNodeDepth,
        @Min(0) Long memoryBytes
) {
}
