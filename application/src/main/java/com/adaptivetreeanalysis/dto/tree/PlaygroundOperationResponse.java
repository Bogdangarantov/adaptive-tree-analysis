package com.adaptivetreeanalysis.dto.tree;

import java.util.List;

public record PlaygroundOperationResponse(
        String treeType,
        TreeOperationType operation,
        int inputKey,
        boolean found,
        boolean changed,
        List<TreeStepEvent> steps,
        TreeMetrics metrics,
        PlaygroundTreeSnapshot snapshot
) {
}
