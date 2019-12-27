package com.github.mhdirkse.countlang.execution;

final class Symbol {
    private String name;
    private CountlangType countlangType;
    private Object value;

    public Symbol(String name) {
        this.name = name;
        this.countlangType = CountlangType.UNKNOWN;
        this.value = null;
    }

    public String getName() {
        return name;
    }

    public CountlangType getCountlangType() {
        return countlangType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if(value == null) {
            throw new NullPointerException();
        }
        CountlangType newCountlangType = CountlangType.typeOf(value);
        if(countlangType == CountlangType.UNKNOWN) {
            this.countlangType = CountlangType.typeOf(value);
        } else if(newCountlangType != countlangType) {
            throw new IllegalArgumentException(String.format(
                    "Attempt to change type of symbol %s from %s to %s",
                    name, countlangType.toString(), newCountlangType.toString()));
        }
        this.value = value;
    }
}