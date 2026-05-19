package com.adaptivetreeanalysis.dto.benchmark;

import java.util.List;

public record BenchmarkHeightGrowthSeriesResponse(
        String treeType,
        List<BenchmarkHeightGrowthPointResponse> points
) {
}
