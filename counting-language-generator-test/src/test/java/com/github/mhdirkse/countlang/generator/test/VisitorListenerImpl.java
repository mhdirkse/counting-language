package com.github.mhdirkse.countlang.generator.test;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

class VisitorListenerImpl implements VisitorListener {
    @Getter
    private List<String> visitedNames = new ArrayList<>();

    @Override
    public void enterCompositeAcceptor(CompositeAcceptor ctx) {
        visitedNames.add(ctx.getName());
    }

    @Override
    public void exitCompositeAcceptor(CompositeAcceptor ctx) {
        visitedNames.add(ctx.getName());
    }

    @Override
    public void visitAtomicAcceptor(AtomicAcceptor ctx) {
        visitedNames.add(ctx.getName());
    }
}
