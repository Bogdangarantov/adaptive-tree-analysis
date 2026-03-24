package com.adaptivetreeanalysis.service.tree;

import com.adaptivetreeanalysis.domain.tree.AvlTree;
import com.adaptivetreeanalysis.domain.tree.IntTree;
import com.adaptivetreeanalysis.domain.tree.RedBlackTree;
import com.adaptivetreeanalysis.domain.tree.SplayTree;
import com.adaptivetreeanalysis.domain.tree.TreeStructureNode;
import com.adaptivetreeanalysis.dto.tree.PlaygroundOperationRequest;
import com.adaptivetreeanalysis.dto.tree.PlaygroundOperationResponse;
import com.adaptivetreeanalysis.dto.tree.PlaygroundTreeNode;
import com.adaptivetreeanalysis.dto.tree.PlaygroundTreeSnapshot;
import com.adaptivetreeanalysis.dto.tree.TreeMetrics;
import com.adaptivetreeanalysis.dto.tree.TreeOperationType;
import com.adaptivetreeanalysis.dto.tree.TreeStepEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PlaygroundTreeOperationService {

    public PlaygroundOperationResponse execute(PlaygroundOperationRequest request) {
        String treeType = normalizeTreeType(request.treeType());
        TreeStructureNode beforeSnapshot = toDomainSnapshot(request.root());
        IntTree tree = restoreTree(treeType, beforeSnapshot);

        int key = request.key();
        TreeOperationType operation = request.operation();

        boolean foundBefore = contains(beforeSnapshot, key);
        List<TreeStepEvent> steps = new ArrayList<>(buildPathSteps(beforeSnapshot, key));

        long startedAt = System.nanoTime();
        switch (operation) {
            case INSERT -> tree.insert(key);
            case DELETE -> tree.delete(key);
            case SEARCH -> tree.search(key);
        }
        long executionTimeNs = System.nanoTime() - startedAt;

        TreeStructureNode afterSnapshot = tree.snapshot();
        boolean changed = !equalsSnapshot(beforeSnapshot, afterSnapshot);

        steps.add(buildOutcomeStep(operation, key, foundBefore, changed));
        Integer beforeRootKey = beforeSnapshot != null ? beforeSnapshot.key() : null;
        Integer afterRootKey = afterSnapshot != null ? afterSnapshot.key() : null;
        if (beforeRootKey != null || afterRootKey != null) {
            Map<String, Object> rootMetadata = new LinkedHashMap<>();
            rootMetadata.put("previousRootKey", beforeRootKey);
            rootMetadata.put("currentRootKey", afterRootKey);
            rootMetadata.put("changed", changed);
            steps.add(new TreeStepEvent(
                    "root_state",
                    afterRootKey,
                    rootMetadata
            ));
        }

        TreeMetrics metrics = new TreeMetrics(
                executionTimeNs,
                tree.rotationCount(),
                tree.height(),
                1
        );

        return new PlaygroundOperationResponse(
                treeType,
                operation,
                key,
                foundBefore,
                changed,
                steps,
                metrics,
                new PlaygroundTreeSnapshot(treeType, toDtoSnapshot(afterSnapshot))
        );
    }

    private IntTree restoreTree(String treeType, TreeStructureNode snapshot) {
        return switch (treeType) {
            case "avl" -> AvlTree.fromSnapshot(snapshot);
            case "red-black" -> RedBlackTree.fromSnapshot(snapshot);
            case "splay" -> SplayTree.fromSnapshot(snapshot);
            default -> throw new IllegalArgumentException("Unsupported tree type: " + treeType);
        };
    }

    private String normalizeTreeType(String treeType) {
        if (treeType == null) {
            throw new IllegalArgumentException("Tree type must be provided");
        }

        String normalized = treeType.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "avl", "red-black", "splay" -> normalized;
            default -> throw new IllegalArgumentException("Unsupported tree type: " + treeType);
        };
    }

    private List<TreeStepEvent> buildPathSteps(TreeStructureNode root, int key) {
        List<TreeStepEvent> steps = new ArrayList<>();
        TreeStructureNode current = root;

        while (current != null) {
            steps.add(new TreeStepEvent(
                    "visit_node",
                    current.key(),
                    Map.of(
                            "targetKey", key,
                            "comparison", Integer.compare(key, current.key())
                    )
            ));

            if (key == current.key()) {
                break;
            }

            current = key < current.key() ? current.left() : current.right();
        }

        if (root == null) {
            steps.add(new TreeStepEvent(
                    "visit_empty_tree",
                    null,
                    Map.of("targetKey", key)
            ));
        }

        return steps;
    }

    private TreeStepEvent buildOutcomeStep(TreeOperationType operation, int key, boolean foundBefore, boolean changed) {
        return switch (operation) {
            case INSERT -> new TreeStepEvent(
                    foundBefore ? "insert_skipped" : "insert_node",
                    key,
                    Map.of("alreadyPresent", foundBefore, "changed", changed)
            );
            case DELETE -> new TreeStepEvent(
                    foundBefore ? "delete_node" : "delete_miss",
                    key,
                    Map.of("found", foundBefore, "changed", changed)
            );
            case SEARCH -> new TreeStepEvent(
                    "search_result",
                    key,
                    Map.of("found", foundBefore, "changed", changed)
            );
        };
    }

    private boolean contains(TreeStructureNode root, int key) {
        TreeStructureNode current = root;
        while (current != null) {
            if (key < current.key()) {
                current = current.left();
            } else if (key > current.key()) {
                current = current.right();
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean equalsSnapshot(TreeStructureNode left, TreeStructureNode right) {
        return left == null ? right == null : left.equals(right);
    }

    private TreeStructureNode toDomainSnapshot(PlaygroundTreeNode node) {
        if (node == null || node.key() == null) {
            return null;
        }

        return new TreeStructureNode(
                node.key(),
                node.color(),
                node.height(),
                toDomainSnapshot(node.left()),
                toDomainSnapshot(node.right())
        );
    }

    private PlaygroundTreeNode toDtoSnapshot(TreeStructureNode node) {
        if (node == null) {
            return null;
        }

        return new PlaygroundTreeNode(
                node.key(),
                node.color(),
                node.height(),
                toDtoSnapshot(node.left()),
                toDtoSnapshot(node.right())
        );
    }
}
