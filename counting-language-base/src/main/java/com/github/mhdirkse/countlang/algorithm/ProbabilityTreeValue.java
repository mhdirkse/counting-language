package com.github.mhdirkse.countlang.algorithm;

import lombok.Getter;

public class ProbabilityTreeValue implements Comparable<ProbabilityTreeValue> {
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

    public Object getValue() {
        if(unknown) {
            throw new IllegalStateException("The unknown ProbabilityTreeValue has no normal value");
        }
        return value;
    }

    @Override
    public int compareTo(ProbabilityTreeValue other) {
        if(this.isUnknown()) {
            if(other.isUnknown()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if(other.isUnknown()) {
                return -1;
            } else {
                return compareValue(other);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private int compareValue(ProbabilityTreeValue other) {
        return ((Comparable<Object>) this.getValue()).compareTo(other.getValue());
    }

    @Override
    public String toString() {
        if(isUnknown()) {
            return "unknown";
        } else {
            return getValue().toString();
        }
    }
}
