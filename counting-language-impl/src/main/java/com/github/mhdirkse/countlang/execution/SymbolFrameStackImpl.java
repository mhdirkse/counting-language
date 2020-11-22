package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.utils.Stack;

public abstract class SymbolFrameStackImpl<T, F extends SymbolFrame<T>> implements SymbolFrameStack<T> {
    private final Stack<F> frameStack;

    SymbolFrameStackImpl() {
        frameStack = new Stack<>();
    }

    // Only works if F = SymbolFrameStackExecute
    @SuppressWarnings("unchecked")
    SymbolFrameStackImpl(SymbolFrameStackImpl<T, F> orig) {
        final Stack<F> copied = new Stack<F>();
        orig.frameStack.forEach(f -> copied.push((F) new SymbolFrameExecute((SymbolFrameExecute) f)));
        frameStack = copied;
    }

    /// Only for testing purposes
    SymbolFrameStackImpl(Stack<F> frameStack) {
        this.frameStack = frameStack;
    }

    @Override
    public final void pushFrame(StackFrameAccess stackFrameAccess) {
        frameStack.push(create(stackFrameAccess));
    }

    @Override
    public final void popFrame() {
        frameStack.pop();
    }

    @Override
    public final T read(String name, int line, int column) {
        return findFrame(name).read(name, line, column);
    }

    @Override
    public final void write(String name, T value, int line, int column) {
        findFrame(name).write(name, value, line, column);
    }

    abstract F create(StackFrameAccess access);

    final SymbolFrame<T> findFrame(String name) {
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
        Iterator<F> it = frameStack.topToBottomIterator();
        while(it.hasNext()) {
            SymbolFrame<T> current = it.next();
            result.add(current);
            if(current.getAccess() == StackFrameAccess.HIDE_PARENT) {
                break;
            }
        }
        return result;
    }

    @Override
    public void onSwitchOpened() {
        frameStack.forEach(frame -> frame.onSwitchOpened());
    }
    
    @Override
    public void onSwitchClosed() {
        frameStack.forEach(frame -> frame.onSwitchClosed());
    }
    
    @Override
    public void onBranchOpened() {
        frameStack.forEach(frame -> frame.onBranchOpened());
    }
    
    @Override
    public void onBranchClosed() {
        frameStack.forEach(frame -> frame.onBranchClosed());
    }
}
