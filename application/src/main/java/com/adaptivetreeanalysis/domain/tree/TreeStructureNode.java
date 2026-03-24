package com.adaptivetreeanalysis.domain.tree;

/**
 * Immutable tree snapshot node used to transfer exact structure
 * between the stateless playground API and tree domain objects.
 */
public record TreeStructureNode(
        int key,
        String color,
        Integer height,
        TreeStructureNode left,
        TreeStructureNode right
) {
}
