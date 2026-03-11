package com.adaptivetreeanalysis.domain.tree;

import java.util.ArrayDeque;

/**
 * Цілочисельне Splay-дерево (self-adjusting), яке ми використовуємо як "extensible" дерево
 * у контексті бенчмарків.
 */
public class SplayTree implements IntTree {

    private static final class Node {
        int key;
        Node left;
        Node right;

        Node(int key) {
            this.key = key;
        }
    }

    private Node root;
    private int rotationCount;

    @Override
    public void insert(int key) {
        if (root == null) {
            root = new Node(key);
            return;
        }

        root = splay(root, key);
        if (root.key == key) {
            return;
        }

        Node node = new Node(key);
        if (key < root.key) {
            node.right = root;
            node.left = root.left;
            root.left = null;
        } else {
            node.left = root;
            node.right = root.right;
            root.right = null;
        }
        root = node;
    }

    @Override
    public boolean search(int key) {
        if (root == null) {
            return false;
        }
        root = splay(root, key);
        return root.key == key;
    }

    @Override
    public boolean delete(int key) {
        if (root == null) {
            return false;
        }
        root = splay(root, key);
        if (root.key != key) {
            return false;
        }

        if (root.left == null) {
            root = root.right;
        } else {
            Node rightSubtree = root.right;
            root = root.left;
            root = splay(root, key);
            root.right = rightSubtree;
        }
        return true;
    }

    /**
     * Ітеративний top-down splay (Sleator-Tarjan), без рекурсії.
     */
    private Node splay(Node node, int key) {
        if (node == null) {
            return null;
        }

        Node header = new Node(0); // тимчасовий корінь
        Node leftTreeMax = header;
        Node rightTreeMin = header;

        Node current = node;

        while (true) {
            if (key < current.key) {
                if (current.left == null) {
                    break;
                }
                if (key < current.left.key) {
                    // zig-zig: rotate right
                    Node y = current.left;
                    current.left = y.right;
                    y.right = current;
                    current = y;
                    rotationCount++;
                    if (current.left == null) {
                        break;
                    }
                }
                // link right
                rightTreeMin.left = current;
                rightTreeMin = current;
                current = current.left;
            } else if (key > current.key) {
                if (current.right == null) {
                    break;
                }
                if (key > current.right.key) {
                    // zig-zig: rotate left
                    Node y = current.right;
                    current.right = y.left;
                    y.left = current;
                    current = y;
                    rotationCount++;
                    if (current.right == null) {
                        break;
                    }
                }
                // link left
                leftTreeMax.right = current;
                leftTreeMax = current;
                current = current.right;
            } else {
                break;
            }
        }

        // збираємо дерево назад
        leftTreeMax.right = current.left;
        rightTreeMin.left = current.right;
        current.left = header.right;
        current.right = header.left;
        return current;
    }

    @Override
    public int height() {
        DepthStats stats = analyzeTree();
        return stats.count == 0 ? 0 : stats.maxDepth + 1;
    }

    @Override
    public int rotationCount() {
        return rotationCount;
    }

    @Override
    public int nodeCount() {
        return analyzeTree().count;
    }

    @Override
    public int maxDepth() {
        return analyzeTree().maxDepth;
    }

    @Override
    public double avgDepth() {
        if (root == null) {
            return 0.0;
        }
        DepthStats stats = analyzeTree();
        return stats.count == 0 ? 0.0 : (double) stats.sum / stats.count;
    }

    private static final class DepthStats {
        long sum;
        int count;
        int maxDepth;
    }

    private DepthStats analyzeTree() {
        DepthStats stats = new DepthStats();
        if (root == null) {
            return stats;
        }

        ArrayDeque<NodeDepth> stack = new ArrayDeque<>();
        stack.push(new NodeDepth(root, 0));

        while (!stack.isEmpty()) {
            NodeDepth current = stack.pop();
            stats.count++;
            stats.sum += current.depth;
            stats.maxDepth = Math.max(stats.maxDepth, current.depth);

            if (current.node.right != null) {
                stack.push(new NodeDepth(current.node.right, current.depth + 1));
            }
            if (current.node.left != null) {
                stack.push(new NodeDepth(current.node.left, current.depth + 1));
            }
        }

        return stats;
    }

    private static final class NodeDepth {
        private final Node node;
        private final int depth;

        private NodeDepth(Node node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }
}
