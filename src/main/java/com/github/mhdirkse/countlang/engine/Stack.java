package com.github.mhdirkse.countlang.engine;

import java.util.ArrayDeque;
import java.util.Deque;

final class Stack {
    private Deque<Object> stack = new ArrayDeque<Object>();

    void push(final Object item) {
        stack.addLast(item);
    }

    Object pop() {
        return stack.removeLast();
    }
}
