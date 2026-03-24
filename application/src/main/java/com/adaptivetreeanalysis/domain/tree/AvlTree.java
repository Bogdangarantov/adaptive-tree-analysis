package com.adaptivetreeanalysis.domain.tree;

import java.util.ArrayDeque;

/**
 * Просте цілочисельне AVL-дерево для бенчмарків.
 * Реалізація орієнтована на коректність і збір метрик, а не на всі можливі оптимізації.
 */
public class AvlTree implements IntTree {

    private static final class Node {
        int key;
        Node left;
        Node right;
        int height;

        Node(int key) {
            this.key = key;
            this.height = 1;
        }
    }

    private Node root;
    private int rotationCount;

    public static AvlTree fromSnapshot(TreeStructureNode snapshot) {
        AvlTree tree = new AvlTree();
        tree.root = restore(snapshot);
        return tree;
    }

    @Override
    public void insert(int key) {
        root = insert(root, key);
    }

    private Node insert(Node node, int key) {
        if (node == null) {
            return new Node(key);
        }

        if (key < node.key) {
            node.left = insert(node.left, key);
        } else if (key > node.key) {
            node.right = insert(node.right, key);
        } else {
            // дублікати ігноруємо
            return node;
        }

        updateHeight(node);
        return rebalance(node);
    }

    @Override
    public boolean search(int key) {
        Node current = root;
        while (current != null) {
            if (key < current.key) {
                current = current.left;
            } else if (key > current.key) {
                current = current.right;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int key) {
        int before = nodeCount();
        root = delete(root, key);
        return nodeCount() < before;
    }

    private Node delete(Node node, int key) {
        if (node == null) {
            return null;
        }

        if (key < node.key) {
            node.left = delete(node.left, key);
        } else if (key > node.key) {
            node.right = delete(node.right, key);
        } else {
            // вузол знайдено
            if (node.left == null || node.right == null) {
                Node child = node.left != null ? node.left : node.right;
                node = child;
            } else {
                // два нащадки: беремо мінімум справа
                Node successor = minValueNode(node.right);
                node.key = successor.key;
                node.right = delete(node.right, successor.key);
            }
        }

        if (node == null) {
            return null;
        }

        updateHeight(node);
        return rebalance(node);
    }

    private Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    private void updateHeight(Node node) {
        int leftH = node.left != null ? node.left.height : 0;
        int rightH = node.right != null ? node.right.height : 0;
        node.height = 1 + Math.max(leftH, rightH);
    }

    private int getBalance(Node node) {
        if (node == null) {
            return 0;
        }
        int leftH = node.left != null ? node.left.height : 0;
        int rightH = node.right != null ? node.right.height : 0;
        return leftH - rightH;
    }

    private Node rebalance(Node node) {
        int balance = getBalance(node);

        // Left heavy
        if (balance > 1) {
            if (getBalance(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        // Right heavy
        if (balance < -1) {
            if (getBalance(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }

        return node;
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t2 = x.right;

        x.right = y;
        y.left = t2;

        updateHeight(y);
        updateHeight(x);

        rotationCount++;
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t2 = y.left;

        y.left = x;
        x.right = t2;

        updateHeight(x);
        updateHeight(y);

        rotationCount++;
        return y;
    }

    @Override
    public int height() {
        return root == null ? 0 : root.height;
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

    @Override
    public TreeStructureNode snapshot() {
        return snapshot(root);
    }

    private static final class DepthStats {
        long sum;
        int count;
        int maxDepth;
    }

    private static Node restore(TreeStructureNode snapshot) {
        if (snapshot == null) {
            return null;
        }

        Node node = new Node(snapshot.key());
        node.left = restore(snapshot.left());
        node.right = restore(snapshot.right());
        updateHeightStatic(node);
        return node;
    }

    private static TreeStructureNode snapshot(Node node) {
        if (node == null) {
            return null;
        }
        return new TreeStructureNode(
                node.key,
                null,
                node.height,
                snapshot(node.left),
                snapshot(node.right)
        );
    }

    private static void updateHeightStatic(Node node) {
        int leftH = node.left != null ? node.left.height : 0;
        int rightH = node.right != null ? node.right.height : 0;
        node.height = 1 + Math.max(leftH, rightH);
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
