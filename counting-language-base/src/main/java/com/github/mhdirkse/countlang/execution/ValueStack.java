package com.github.mhdirkse.countlang.execution;

import java.util.ArrayDeque;
import java.util.Deque;

class ValueStack {
    private final Deque<Object> stack = new ArrayDeque<>();
    
    public void push(Object item) {
        stack.addLast(item);
    }

    public Object pop() {
        return stack.removeLast();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
