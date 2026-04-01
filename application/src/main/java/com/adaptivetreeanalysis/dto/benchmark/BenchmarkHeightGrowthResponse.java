package com.adaptivetreeanalysis.dto.benchmark;

import java.util.List;

public record BenchmarkHeightGrowthResponse(
        long totalRuns,
        List<BenchmarkHeightGrowthSeriesResponse> series
) {
}
