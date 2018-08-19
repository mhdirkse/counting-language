package com.github.mhdirkse.countlang.generator.test.input;

import java.util.List;

import lombok.Setter;

public class CompositeAcceptor extends Acceptor implements Composite {
    @Override
    public void accept(Visitor v) {
        v.visitCompositeAcceptor(this);
    }

    @Setter
    private List<Acceptor> children;

    @Override
    public List<Acceptor> getChildren() {
        return children;
    }
}
