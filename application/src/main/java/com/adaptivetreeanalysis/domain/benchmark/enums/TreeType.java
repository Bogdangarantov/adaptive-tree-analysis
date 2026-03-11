package com.adaptivetreeanalysis.domain.benchmark.enums;

public enum TreeType {
    AVL("avl"),
    RED_BLACK("red_black"),
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

        for (TreeType type : values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unsupported treeType: " + value);
    }
}
