package com.adaptivetreeanalysis.domain.tree;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.Random;
import org.junit.jupiter.api.Test;

class RedBlackTreeTest {

    @Test
    void benchmarkLikeMixedOperationsCompleteWithoutHanging() {
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            RedBlackTree tree = new RedBlackTree();
            int size = 10_001;
            int[] keys = generateKeys(size);
            int[] operations = generateOperations(size, 40, 50, 10);

            for (int i = 0; i < size; i++) {
                switch (operations[i]) {
                    case 0 -> tree.insert(keys[i]);
                    case 1 -> tree.search(keys[i]);
                    case 2 -> tree.delete(keys[i]);
                    default -> throw new IllegalStateException("Unexpected operation code: " + operations[i]);
                }
            }

            tree.height();
            tree.nodeCount();
            tree.maxDepth();
            tree.avgDepth();
        });
    }

    private int[] generateKeys(int size) {
        int[] keys = new int[size];
        Random random = new Random(42L);
        for (int i = 0; i < size; i++) {
            keys[i] = random.nextInt(size * 10 + 1);
        }
        return keys;
    }

    private int[] generateOperations(int size, int insertPercent, int searchPercent, int deletePercent) {
        int[] operations = new int[size];
        int total = insertPercent + searchPercent + deletePercent;
        int insertThreshold = insertPercent;
        int searchThreshold = insertPercent + searchPercent;
        Random random = new Random(123L);

        for (int i = 0; i < size; i++) {
            int value = random.nextInt(total);
            if (value < insertThreshold) {
                operations[i] = 0;
            } else if (value < searchThreshold) {
                operations[i] = 1;
            } else {
                operations[i] = 2;
            }
        }
        return operations;
    }
}
