package com.adaptivetreeanalysis.dto.tree;

import java.util.List;

public record AvlOperationResponse(
        String treeType,
        TreeOperationType operation,
        int inputKey,
        List<TreeStepEvent> steps,
        TreeMetrics metrics
) {
}
