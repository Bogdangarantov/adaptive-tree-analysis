package com.adaptivetreeanalysis.domain.benchmark.enums;

public enum ExperimentStatus {
    PENDING("pending"),
    RUNNING("running"),
    COMPLETED("completed"),
    FAILED("failed");

    private final String value;

    ExperimentStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ExperimentStatus fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        for (ExperimentStatus status : values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unsupported experiment status: " + value);
    }
}
