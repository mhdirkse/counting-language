package com.github.mhdirkse.countlang.tasks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Supplier;

class VariableCheckContext {
    private final Supplier<VariableCheckFrame> frameFactory;
    private final Deque<VariableCheckFrame> frameStack = new ArrayDeque<>();
    private final List<VariableCheckFrame> allFrames = new ArrayList<>();

    VariableCheckContext(Supplier<VariableCheckFrame> frameFactory) {
        this.frameFactory = frameFactory;
    }

    VariableCheckContext() {
        this(VariableCheckFrameImpl::new);
    }

    void define(final String name, final int line, final int column) {
        frameStack.getFirst().define(name, line, column);
    }

    void use(final String name, final int line, final int column) {
        frameStack.getFirst().use(name, line, column);
    }

    void pushNewFrame() {
        VariableCheckFrame newFrame = frameFactory.get();
        frameStack.addFirst(newFrame);
        allFrames.add(newFrame);
    }

    void popFrame() {
        frameStack.removeFirst();
    }

    void report(final StatusReporter reporter) {
        allFrames.forEach(f -> f.report(reporter));
    }
}
