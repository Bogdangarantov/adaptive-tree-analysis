package com.adaptivetreeanalysis.domain.tree;

import java.util.ArrayDeque;

/**
 * Цілочисельне червоно-чорне дерево для бенчмарків.
 * Реалізація базується на класичному алгоритмі вставки/видалення (CLRS),
 * з підрахунком ротацій.
 */
public class RedBlackTree implements IntTree {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static final class Node {
        int key;
        Node left;
        Node right;
        Node parent;
        boolean color = RED;

        Node(int key) {
            this.key = key;
        }
    }

    private Node root;
    private int rotationCount;

    @Override
    public void insert(int key) {
        Node z = new Node(key);
        Node y = null;
        Node x = root;

        while (x != null) {
            y = x;
            if (key < x.key) {
                x = x.left;
            } else if (key > x.key) {
                x = x.right;
            } else {
                // вже існує, не вставляємо дублікати
                return;
            }
        }

        z.parent = y;
        if (y == null) {
            root = z;
        } else if (key < y.key) {
            y.left = z;
        } else {
            y.right = z;
        }

        z.color = RED;
        insertFixup(z);
    }

    private void insertFixup(Node z) {
        while (z.parent != null && z.parent.color == RED) {
            if (z.parent == grandparent(z).left) {
                Node y = uncle(z);
                if (y != null && y.color == RED) {
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    grandparent(z).color = RED;
                    z = grandparent(z);
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        rotateLeft(z);
                    }
                    z.parent.color = BLACK;
                    grandparent(z).color = RED;
                    rotateRight(grandparent(z));
                }
            } else {
                Node y = uncle(z);
                if (y != null && y.color == RED) {
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    grandparent(z).color = RED;
                    z = grandparent(z);
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rotateRight(z);
                    }
                    z.parent.color = BLACK;
                    grandparent(z).color = RED;
                    rotateLeft(grandparent(z));
                }
            }
        }
        if (root != null) {
            root.color = BLACK;
        }
    }

    private Node grandparent(Node n) {
        return n.parent != null ? n.parent.parent : null;
    }

    private Node uncle(Node n) {
        Node g = grandparent(n);
        if (g == null) {
            return null;
        }
        if (n.parent == g.left) {
            return g.right;
        } else {
            return g.left;
        }
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
        Node z = root;
        while (z != null && z.key != key) {
            if (key < z.key) {
                z = z.left;
            } else {
                z = z.right;
            }
        }
        if (z == null) {
            return false;
        }
        deleteNode(z);
        return true;
    }

    private void deleteNode(Node z) {
        Node y = z;
        Node x;
        Node xParent;
        boolean yOriginalColor = y.color;

        if (z.left == null) {
            x = z.right;
            xParent = z.parent;
            transplant(z, z.right);
        } else if (z.right == null) {
            x = z.left;
            xParent = z.parent;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == z) {
                xParent = y;
                if (x != null) {
                    x.parent = y;
                }
            } else {
                xParent = y.parent;
                transplant(y, y.right);
                y.right = z.right;
                if (y.right != null) {
                    y.right.parent = y;
                }
            }
            transplant(z, y);
            y.left = z.left;
            if (y.left != null) {
                y.left.parent = y;
            }
            y.color = z.color;
        }

        if (yOriginalColor == BLACK) {
            deleteFixup(x, xParent);
        }
    }

    private void deleteFixup(Node x, Node parent) {
        while (x != root && (x == null || x.color == BLACK)) {
            if (parent == null) {
                break;
            }
            if (x == (parent != null ? parent.left : null)) {
                Node w = parent != null ? parent.right : null;
                if (w != null && w.color == RED) {
                    w.color = BLACK;
                    parent.color = RED;
                    rotateLeft(parent);
                    w = parent.right;
                }
                if ((w == null || colorOf(w.left) == BLACK) && (w == null || colorOf(w.right) == BLACK)) {
                    if (w != null) {
                        w.color = RED;
                    }
                    x = parent;
                    parent = x != null ? x.parent : null;
                } else {
                    if (w == null) {
                        break;
                    }
                    if (colorOf(w.right) == BLACK) {
                        if (w.left != null) {
                            w.left.color = BLACK;
                        }
                        w.color = RED;
                        rotateRight(w);
                        w = parent.right;
                    }
                    if (w != null) {
                        w.color = parent.color;
                    }
                    parent.color = BLACK;
                    if (w != null && w.right != null) {
                        w.right.color = BLACK;
                    }
                    rotateLeft(parent);
                    x = root;
                    break;
                }
            } else {
                Node w = parent != null ? parent.left : null;
                if (w != null && w.color == RED) {
                    w.color = BLACK;
                    parent.color = RED;
                    rotateRight(parent);
                    w = parent.left;
                }
                if ((w == null || colorOf(w.right) == BLACK) && (w == null || colorOf(w.left) == BLACK)) {
                    if (w != null) {
                        w.color = RED;
                    }
                    x = parent;
                    parent = x != null ? x.parent : null;
                } else {
                    if (w == null) {
                        break;
                    }
                    if (colorOf(w.left) == BLACK) {
                        if (w.right != null) {
                            w.right.color = BLACK;
                        }
                        w.color = RED;
                        rotateLeft(w);
                        w = parent.left;
                    }
                    if (w != null) {
                        w.color = parent.color;
                    }
                    parent.color = BLACK;
                    if (w != null && w.left != null) {
                        w.left.color = BLACK;
                    }
                    rotateRight(parent);
                    x = root;
                    break;
                }
            }
        }
        if (x != null) {
            x.color = BLACK;
        }
        if (root != null) {
            root.color = BLACK;
        }
    }

    private boolean colorOf(Node n) {
        return n != null && n.color == RED;
    }

    private void transplant(Node u, Node v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
        }
    }

    private Node minimum(Node n) {
        Node current = n;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;

        rotationCount++;
    }

    private void rotateRight(Node y) {
        Node x = y.left;
        y.left = x.right;
        if (x.right != null) {
            x.right.parent = y;
        }
        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y == y.parent.left) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }
        x.right = y;
        y.parent = x;

        rotationCount++;
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
