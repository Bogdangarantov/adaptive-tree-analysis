package com.adaptivetreeanalysis.dto.benchmark;

import java.util.UUID;

public record BenchmarkProgressDto(
        UUID experimentId,
        long doneOperations,
        long totalOperations
) {
}

