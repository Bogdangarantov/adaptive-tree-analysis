package com.adaptivetreeanalysis.domain.tree;

/**
 * Integer search tree interface with basic operations and statistics
 * used for benchmarking.
 */
public interface IntTree {

    void insert(int key);

    boolean search(int key);

    boolean delete(int key);

    int height();

    int rotationCount();

    int nodeCount();

    int maxDepth();

    double avgDepth();
}

