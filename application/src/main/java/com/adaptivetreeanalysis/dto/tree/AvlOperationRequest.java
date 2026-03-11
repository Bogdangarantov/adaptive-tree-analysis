package com.adaptivetreeanalysis.dto.tree;

import jakarta.validation.constraints.NotNull;

public record AvlOperationRequest(
        @NotNull TreeOperationType operation,
        @NotNull Integer key
) {
}
