package com.github.mhdirkse.countlang.ast;

public final class Value {
    private int value;
    private int seq;

    public Value(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(final int seq) {
        this.seq = seq;
    }
}
