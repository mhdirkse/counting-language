package com.github.mhdirkse.countlang.execution;

import java.util.ArrayDeque;
import java.util.Deque;

final class Scope {
    private final Deque<StackFrame> frames = new ArrayDeque<>();

    Scope() {
        frames.addLast(new StackFrame());
    }

    public boolean hasSymbol(String name) {
        return frames.getLast().hasSymbol(name);
    }

    public Object getValue(String name) {
        return frames.getLast().getValue(name);
    }

    public CountlangType getCountlangType(String name) {
        return frames.getLast().getCountlangType(name);
    }

    public void putSymbol(String name, Object value) {
        frames.getLast().putSymbol(name, value);
    }

    public void pushFrame(StackFrame frame) {
        frames.addLast(frame);
    }

    public void popFrame() {
        frames.removeLast();
    }
}
