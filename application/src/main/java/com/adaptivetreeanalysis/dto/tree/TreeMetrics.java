package com.adaptivetreeanalysis.dto.tree;

public record TreeMetrics(
        long executionTimeNs,
        int rotationCount,
        int treeHeight,
        int operationCount
) {
}
