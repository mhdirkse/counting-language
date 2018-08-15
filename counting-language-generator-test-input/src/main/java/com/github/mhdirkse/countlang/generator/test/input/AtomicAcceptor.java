package com.github.mhdirkse.countlang.generator.test.input;

public class AtomicAcceptor extends Acceptor {
    @Override
    public void accept(Visitor v) {
        v.visitAtomicAcceptor(this);
    }
}
