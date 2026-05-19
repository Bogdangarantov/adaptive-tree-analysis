package com.adaptivetreeanalysis.dto.benchmark;

public record BenchmarkHeightGrowthPointResponse(
        Integer datasetSize,
        double averageTreeHeight,
        long runCount
) {
}
