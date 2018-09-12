package com.github.mhdirkse.countlang.tasks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Supplier;

class VariableCheckContextImpl implements VariableCheckContext {
    private final Supplier<VariableCheckFrame> frameFactory;
    private final Deque<VariableCheckFrame> frameStack = new ArrayDeque<>();
    private final List<VariableCheckFrame> allFrames = new ArrayList<>();

    VariableCheckContextImpl(Supplier<VariableCheckFrame> frameFactory) {
        this.frameFactory = frameFactory;
    }

    VariableCheckContextImpl() {
        this(VariableCheckFrameImpl::new);
    }

    @Override
    public void define(final String name, final int line, final int column) {
        frameStack.getFirst().define(name, line, column);
    }

    @Override
    public void use(final String name, final int line, final int column) {
        frameStack.getFirst().use(name, line, column);
    }

    @Override
    public void pushNewFrame() {
        VariableCheckFrame newFrame = frameFactory.get();
        frameStack.addFirst(newFrame);
        allFrames.add(newFrame);
    }

    @Override
    public void popFrame() {
        frameStack.removeFirst();
    }

    @Override
    public void report(final StatusReporter reporter) {
        allFrames.forEach(f -> f.report(reporter));
    }
}
