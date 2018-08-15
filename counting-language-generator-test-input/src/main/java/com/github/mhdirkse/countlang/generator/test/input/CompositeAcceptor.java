package com.github.mhdirkse.countlang.generator.test.input;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CompositeAcceptor extends Acceptor {
    @Override
    public void accept(Visitor v) {
        v.visitCompositeAcceptor(this);
    }

    @Getter
    @Setter
    private List<Acceptor> children;
}
