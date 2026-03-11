package com.adaptivetreeanalysis.dto.benchmark;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BenchmarkQuickRunRequest(
        @NotNull @Min(1) Integer datasetSize,
        /**
         * One of DatasetDistributionType values: RANDOM, SORTED, REVERSE_SORTED, ALMOST_SORTED, ZIPF, CUSTOM.
         * Case-insensitive; will be parsed on the backend.
         */
        @NotNull String distributionType,
        @Min(0) @Max(100) Integer insertPercent,
        @Min(0) @Max(100) Integer searchPercent,
        @Min(0) @Max(100) Integer deletePercent,
        @NotNull @Min(1) Integer repeatCount
) {

    public int totalPercent() {
        int insert = insertPercent == null ? 0 : insertPercent;
        int search = searchPercent == null ? 0 : searchPercent;
        int delete = deletePercent == null ? 0 : deletePercent;
        return insert + search + delete;
    }
}

