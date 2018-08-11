package com.github.mhdirkse.countlang.generator.test;

public class AcceptorWalker {
    private Acceptor a;

    AcceptorWalker(final Acceptor a) {
        this.a = a;
    }

    void walk(final VisitorListener l) {
        VisitorToListener v = new VisitorToListener(l);
        a.accept(v);
    }
}
