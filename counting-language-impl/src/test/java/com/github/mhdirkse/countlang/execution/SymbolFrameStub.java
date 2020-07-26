package com.github.mhdirkse.countlang.execution;

class SymbolFrameStub implements SymbolFrame<DummyValue> {
    static final String SYMBOL = "x";
    private final boolean hasSymbol;
    private final StackFrameAccess access;

    private int seq;

    SymbolFrameStub(final StackFrameAccess access, final boolean hasSymbol) {
        this.access = access;
        this.hasSymbol = hasSymbol;
    }

    @Override
    public boolean has(String name) {
        if(!name.equals(SYMBOL)) {
            throw new IllegalArgumentException("Only symbol " + SYMBOL + " supported");
        }
        return hasSymbol;
    }

    @Override
    public DummyValue read(String name, int line, int column) {
        throw new IllegalStateException("SymbolFrameStub cannot be read");
    }

    @Override
    public void write(String name, DummyValue value, int line, int column) {
        throw new IllegalStateException("SymbolFrameStub cannot be written");
    }

    @Override
    public StackFrameAccess getAccess() {
        return access;
    }

    void setSeq(final int seq) {
        this.seq = seq;
    }

    int getSeq() {
        return seq;
    }
}
