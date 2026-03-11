package com.adaptivetreeanalysis.dto.benchmark;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDatasetRequest(
        @NotBlank String name,
        @NotBlank String distributionType,
        @NotNull @Min(1) Integer size,
        Long seed,
        String payload
) {
}
