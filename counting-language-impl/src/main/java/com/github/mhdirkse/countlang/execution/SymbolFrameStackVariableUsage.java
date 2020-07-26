package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.List;

public class SymbolFrameStackVariableUsage extends SymbolFrameStackImpl<DummyValue, SymbolFrame<DummyValue>>{
    private final List<SymbolFrameVariableUsage> allFrames = new ArrayList<>();
    
    @Override
    SymbolFrame<DummyValue> create(StackFrameAccess access) {
        SymbolFrameVariableUsage frame = new SymbolFrameVariableUsage(access);
        allFrames.add(frame);
        return frame;
    }

    public void listEvents(VariableUsageEventHandler handler) {
        allFrames.forEach(f -> f.listEvents(handler));
    }
}
