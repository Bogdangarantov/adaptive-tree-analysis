package com.adaptivetreeanalysis.dto.benchmark;

import java.util.List;

public record BenchmarkQuickRunResponse(
        ExperimentResponse experiment,
        List<ExperimentStatResponse> stats
) {
}

