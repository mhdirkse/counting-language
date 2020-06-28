package com.github.mhdirkse.countlang.execution;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.StackFrameAccess;

final class Scope {
    private final Deque<StackFrame> frames = new ArrayDeque<>();

    Scope() {
        frames.addLast(new StackFrame(StackFrameAccess.HIDE_PARENT));
    }

    public boolean hasSymbol(String name) {
        return findFrame(name).hasSymbol(name);
    }

    private StackFrame findFrame(String name) {
        Iterator<StackFrame> it = frames.descendingIterator();
        while(it.hasNext()) {
            StackFrame frame = it.next();
            if(frame.hasSymbol(name)) {
                return frame;
            }
            switch(frame.getStackFrameAccess()) {
            case HIDE_PARENT:
                return frames.getLast();
            case SHOW_PARENT:
                break;
            }
        }
        throw new IllegalStateException("Not reachable, because first stack frame hides the (non-existent) parent");
    }

    public Object getValue(String name) {
        return findFrame(name).getValue(name);
    }

    public CountlangType getCountlangType(String name) {
        return findFrame(name).getCountlangType(name);
    }

    public void putSymbol(String name, Object value) {
        findFrame(name).putSymbol(name, value);
    }

    public void pushFrame(StackFrame frame) {
        frames.addLast(frame);
    }

    public void popFrame() {
        frames.removeLast();
    }
}
