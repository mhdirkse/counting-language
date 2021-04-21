package com.github.mhdirkse.countlang.algorithm;

import lombok.Getter;

class ProbabilityTreeValue {
    private final @Getter boolean unknown;
    private final Object value;

    private ProbabilityTreeValue(Object value) {
        this.unknown = false;
        this.value = value;
    }

    private ProbabilityTreeValue() {
        this.unknown = true;
        value = null;
    }

    private static final ProbabilityTreeValue UNKNOWN = new ProbabilityTreeValue();

    static ProbabilityTreeValue unknown() {
        return UNKNOWN;
    }

    static ProbabilityTreeValue of(Object value) {
        if(value == null) {
            throw new IllegalArgumentException("A ProbabilityTreeValue cannot wrap null");
        }
        return new ProbabilityTreeValue(value);
    }

    Object getValue() {
        if(unknown) {
            throw new IllegalStateException("The unknown ProbabilityTreeValue has no normal value");
        }
        return value;
    }
}
