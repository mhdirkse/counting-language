package com.github.mhdirkse.countlang.tasks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import com.github.mhdirkse.countlang.execution.StackFrameAccess;

class VariableCheckContextImpl implements VariableCheckContext {
    private final Function<StackFrameAccess, VariableCheckFrame> frameFactory;
    private final Deque<VariableCheckFrame> frameStack = new ArrayDeque<>();
    private final List<VariableCheckFrame> allFrames = new ArrayList<>();

    VariableCheckContextImpl(Function<StackFrameAccess, VariableCheckFrame> frameFactory) {
        this.frameFactory = frameFactory;
    }

    VariableCheckContextImpl() {
        this(VariableCheckFrameImpl::new);
    }

    @Override
    public void define(final String name, final int line, final int column) {
        findFrame(name).define(name, line, column);
    }

    private VariableCheckFrame findFrame(String name) {
        Iterator<VariableCheckFrame> it = frameStack.descendingIterator();
        while(it.hasNext()) {
            VariableCheckFrame frame = it.next();
            if(frame.hasSymbol(name)) {
                return frame;
            }
            switch(frame.getStackFrameAccess()) {
            case HIDE_PARENT:
                return frameStack.getLast();
            case SHOW_PARENT:
                break;
            }
        }
        throw new IllegalStateException("Not reachable, because first stack frame hides the (non-existent) parent");
    }

    @Override
    public void use(final String name, final int line, final int column) {
        findFrame(name).use(name, line, column);
    }

    @Override
    public void pushNewFrame(final StackFrameAccess stackFrameAccess) {
        VariableCheckFrame newFrame = frameFactory.apply(stackFrameAccess);
        frameStack.addLast(newFrame);
        allFrames.add(newFrame);
    }

    @Override
    public void popFrame() {
        frameStack.removeLast();
    }

    @Override
    public void report(final StatusReporter reporter) {
        allFrames.forEach(f -> f.report(reporter));
    }
}
