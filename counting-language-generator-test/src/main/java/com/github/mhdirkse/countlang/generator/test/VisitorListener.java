package com.github.mhdirkse.countlang.generator.test;

public interface VisitorListener {
    void enterCompositeAcceptor(CompositeAcceptor ctx);
    void exitCompositeAcceptor(CompositeAcceptor ctx);
    void visitAtomicAcceptor(AtomicAcceptor ctx);
}
