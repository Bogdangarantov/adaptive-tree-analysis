package com.adaptivetreeanalysis.dto.benchmark;

public record BenchmarkTreeSummaryResponse(
        String treeType,
        long runCount,
        long totalOperations,
        long averageExecutionTimeNs,
        long bestExecutionTimeNs,
        double averageTreeHeight,
        long averageInsertTimeNs,
        long averageSearchTimeNs,
        long averageDeleteTimeNs
) {
}
