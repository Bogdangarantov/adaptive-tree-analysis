package com.adaptivetreeanalysis.domain.benchmark.enums;

public enum DatasetDistributionType {
    RANDOM("random"),
    SORTED("sorted"),
    REVERSE_SORTED("reverse_sorted"),
    ALMOST_SORTED("almost_sorted"),
    ZIPF("zipf"),
    CUSTOM("custom");

    private final String value;

    DatasetDistributionType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static DatasetDistributionType fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("distributionType must not be null");
        }

        for (DatasetDistributionType type : values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unsupported distributionType: " + value);
    }
}
