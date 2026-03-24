package com.adaptivetreeanalysis.dto.tree;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlaygroundOperationRequest(
        @NotBlank String treeType,
        @NotNull TreeOperationType operation,
        @NotNull Integer key,
        PlaygroundTreeNode root
) {
}
