package com.adaptivetreeanalysis.dto.tree;

public record PlaygroundTreeSnapshot(
        String treeType,
        PlaygroundTreeNode root
) {
}
