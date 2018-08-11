package com.github.mhdirkse.countlang.generator.test;

class VisitorToListener implements Visitor {
    private VisitorListener l;

    VisitorToListener(final VisitorListener l) {
        this.l = l;
    }

    @Override
    public void visitCompositeAcceptor(CompositeAcceptor acceptor) {
        l.enterCompositeAcceptor(acceptor);
        for (Acceptor child : acceptor.getChildren()) {
            child.accept(this);
        }
        l.exitCompositeAcceptor(acceptor);
    }

    @Override
    public void visitAtomicAcceptor(AtomicAcceptor acceptor) {
        l.visitAtomicAcceptor(acceptor);
    }
}
