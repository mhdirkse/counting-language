package com.github.mhdirkse.countlang.generator.test;

public class AtomicAcceptor extends Acceptor {
    @Override
    public void accept(Visitor v) {
        v.visitAtomicAcceptor(this);
    }
}
