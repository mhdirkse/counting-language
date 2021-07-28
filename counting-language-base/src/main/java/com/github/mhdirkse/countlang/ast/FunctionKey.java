package com.github.mhdirkse.countlang.ast;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class FunctionKey {
    private final String name;
    private final CountlangType ownerType;

    /**
     * Create key of function that is not a member.
     */
    public FunctionKey(String name) {
        this.name = name;
        this.ownerType = null;
    }

    /**
     * Create key of a member function.
     */
    public FunctionKey(String name, CountlangType ownerType) {
        this.name = name;
        this.ownerType = ownerType;
    }

    @Override
    public String toString() {
        if(ownerType == null) {
            return name;
        } else {
            return String.format("%s.%s", ownerType.toString(), name);
        }
    }
}
