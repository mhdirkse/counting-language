package com.github.mhdirkse.countlang.generator.test;

import com.github.mhdirkse.countlang.generator.test.input.AtomicAcceptor;
import com.github.mhdirkse.countlang.generator.test.input.CompositeAcceptor;

public interface VisitorListener {
    void enterCompositeAcceptor(CompositeAcceptor ctx);
    void exitCompositeAcceptor(CompositeAcceptor ctx);
    void visitAtomicAcceptor(AtomicAcceptor ctx);
}
