package com.adaptivetreeanalysis.dto.benchmark;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateExperimentRequest(
        @NotNull UUID datasetId,
        @NotBlank String title,
        @NotBlank String operationsProfile,
        String hardwareInfo
) {
}
