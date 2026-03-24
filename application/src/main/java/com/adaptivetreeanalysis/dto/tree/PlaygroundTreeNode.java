package com.adaptivetreeanalysis.dto.tree;

public record PlaygroundTreeNode(
        Integer key,
        String color,
        Integer height,
        PlaygroundTreeNode left,
        PlaygroundTreeNode right
) {
}
