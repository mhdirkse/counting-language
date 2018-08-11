package com.github.mhdirkse.countlang.generator.test;

public interface Visitor {
    void visitCompositeAcceptor(CompositeAcceptor acceptor);
    void visitAtomicAcceptor(AtomicAcceptor acceptor);
}
