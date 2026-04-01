package com.adaptivetreeanalysis.dto.benchmark;

public record BenchmarkHeightGrowthPointResponse(
        int datasetSize,
        double averageTreeHeight,
        long runCount
) {
}
