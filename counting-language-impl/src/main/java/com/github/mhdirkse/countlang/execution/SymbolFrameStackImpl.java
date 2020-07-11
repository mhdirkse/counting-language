package com.github.mhdirkse.countlang.execution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.ast.StackFrameAccess;

public abstract class SymbolFrameStackImpl<T> implements SymbolFrameStack<T> {
    private final Deque<SymbolFrame<T>> frameStack = new ArrayDeque<>();

    @Override
    public void pushFrame(StackFrameAccess stackFrameAccess) {
        frameStack.addLast(create(stackFrameAccess));
    }

    @Override
    public void popFrame() {
        frameStack.removeLast();
    }

    @Override
    public T read(String name, int line, int column) {
        return findFrame(name).read(name, line, column);
    }

    @Override
    public <V extends T> void write(String name, V value, int line, int column) {
        findFrame(name).write(name, value, line, column);
    }

    abstract SymbolFrame<T> create(StackFrameAccess access);

    private SymbolFrame<T> findFrame(String name) {
        List<SymbolFrame<T>> accessibleFrames = getAccessibleFrames();
        if(accessibleFrames.isEmpty()) {
            throw new IllegalStateException("No symbol frames to search for symbol: " + name);
        }
        for(SymbolFrame<T> s: accessibleFrames) {
            if(s.has(name)) {
                return s;
            }
        }
        return accessibleFrames.get(0);
    }

    private List<SymbolFrame<T>> getAccessibleFrames() {
        List<SymbolFrame<T>> result = new ArrayList<>();
        Iterator<SymbolFrame<T>> it = frameStack.descendingIterator();
        while(it.hasNext()) {
            SymbolFrame<T> current = it.next();
            result.add(current);
            if(current.getAccess() == StackFrameAccess.HIDE_PARENT) {
                break;
            }
        }
        return result;
    }
}
