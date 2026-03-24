package com.adaptivetreeanalysis.dto.benchmark;

import java.util.List;

public record BenchmarkSummaryResponse(
        long totalRuns,
        long totalOperations,
        long averageExecutionTimeNs,
        long bestExecutionTimeNs,
        List<BenchmarkTreeSummaryResponse> trees
) {
}
