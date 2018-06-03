package com.github.mhdirkse.countlang.ast;

public final class Symbol {
    private String name;
    private int seq;

    public Symbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(final int seq) {
        this.seq = seq;
    }
}