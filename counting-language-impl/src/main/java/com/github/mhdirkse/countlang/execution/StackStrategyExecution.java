package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.StackFrameAccess;
import com.github.mhdirkse.countlang.ast.StackStrategy;

public class StackStrategyExecution {
    public static void before(final StackStrategy ss, final ExecutionContext e) {
        switch(ss) {
        case NO_NEW_FRAME:
            break;
        case NEW_FRAME_SHOWING_PARENT:
            e.startPreparingNewFrame(StackFrameAccess.SHOW_PARENT);
            e.pushNewFrame();
            break;
        case NEW_FRAME_HIDING_PARENT:
            e.startPreparingNewFrame(StackFrameAccess.HIDE_PARENT);
            e.pushNewFrame();
            break;
        }
    }

    public static void after(final StackStrategy ss,final ExecutionContext e) {
        switch(ss) {    
        case NO_NEW_FRAME:
            break;
        case NEW_FRAME_SHOWING_PARENT:
        case NEW_FRAME_HIDING_PARENT:
            e.popFrame();
            break;
        }
    }
}
