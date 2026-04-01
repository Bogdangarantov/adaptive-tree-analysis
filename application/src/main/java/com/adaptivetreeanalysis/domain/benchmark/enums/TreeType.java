package com.adaptivetreeanalysis.domain.benchmark.enums;

public enum TreeType {
    AVL("avl"),
    RED_BLACK("red-black"),
    SPLAY("splay");

    private final String value;

    TreeType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static TreeType fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("treeType must not be null");
        }

        String normalizedValue = value.trim().replace('_', '-');

        for (TreeType type : values()) {
            if (type.value.equalsIgnoreCase(normalizedValue)
                    || type.name().equalsIgnoreCase(normalizedValue)
                    || type.name().replace('_', '-').equalsIgnoreCase(normalizedValue)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unsupported treeType: " + value);
    }
}
