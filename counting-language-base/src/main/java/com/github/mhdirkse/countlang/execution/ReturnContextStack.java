package com.github.mhdirkse.countlang.execution;

import java.util.ArrayDeque;
import java.util.Deque;

class ReturnContextStack {
    private final Deque<ReturnContext> stack = new ArrayDeque<>();
    
    public void push(final int line, final int column, final boolean withReturnValue) {
        stack.addLast(new ReturnContext(line, column, withReturnValue));
    }

    public void popNoReturn() {
        ReturnContext context = stack.removeLast();
        if(context.expectReturnValue()) {
            throw new ProgramException(context.getLine(), context.getColumn(), "You are neglecting that the statement block returns a value");
        }
    }

    public Object popReturnValue() {
        ReturnContext context = stack.removeLast();
        return context.getReturnValue();
    }

    public void setReturnValue(final Object returnValue) {
        stack.getLast().setReturnValue(returnValue);
    }
}
