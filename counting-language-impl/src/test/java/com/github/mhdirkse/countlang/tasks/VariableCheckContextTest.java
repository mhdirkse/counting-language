package com.github.mhdirkse.countlang.tasks;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictControl;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.execution.StackFrameAccess;

public class VariableCheckContextTest {
    private static final String NAME = "xyz";
    private static final int LINE = 2;
    private static final int COLUMN = 3;

    private VariableCheckFrame topLevelFrameStrict;
    private VariableCheckFrame functionFrameStrict;
    private IMocksControl ctrl;
    private StatusReporter reporter;
    private List<VariableCheckFrame> mockFrames;
    private int mockFrameIndex;
    private VariableCheckContext instance;

    @Before
    public void setUp() {
        ctrl = createStrictControl();
        topLevelFrameStrict = ctrl.createMock(VariableCheckFrame.class);
        functionFrameStrict = ctrl.createMock(VariableCheckFrame.class);
        reporter = createNiceMock(StatusReporter.class);
        mockFrameIndex = 0;
        instance = new VariableCheckContextImpl(this::nextFrame);
    }

    VariableCheckFrame nextFrame(final StackFrameAccess stackFrameAccess) {
        return mockFrames.get(mockFrameIndex++);
    }

    @Test
    public void testDelegatesToFirstFrame() {
        matchExercise();
        ctrl.replay();
        exercise();
        ctrl.verify();
    }

    private void matchExercise() {
        mockFrames = Arrays.asList(topLevelFrameStrict, functionFrameStrict);
        EasyMock.expect(topLevelFrameStrict.hasSymbol(NAME)).andReturn(false);
        EasyMock.expect(topLevelFrameStrict.getStackFrameAccess()).andReturn(StackFrameAccess.HIDE_PARENT);
        topLevelFrameStrict.define(anyObject(String.class), anyInt(), anyInt());
        EasyMock.expect(functionFrameStrict.hasSymbol(NAME)).andReturn(false);
        EasyMock.expect(functionFrameStrict.getStackFrameAccess()).andReturn(StackFrameAccess.HIDE_PARENT);
        functionFrameStrict.define(anyObject(String.class), anyInt(), anyInt());
        EasyMock.expect(topLevelFrameStrict.hasSymbol(NAME)).andReturn(true);
        topLevelFrameStrict.define(anyObject(String.class), anyInt(), anyInt());
    }

    private void exercise() {
        instance.pushNewFrame(StackFrameAccess.HIDE_PARENT);
        instance.define(NAME, LINE, COLUMN);
        instance.pushNewFrame(StackFrameAccess.HIDE_PARENT);
        instance.define(NAME, LINE, COLUMN);
        instance.popFrame();
        instance.define(NAME, LINE, COLUMN);
        instance.popFrame();
    }

    @Test
    public void testWhenMultipleFramesCreatedThenMultipleReported() {
        matchExercise();
        topLevelFrameStrict.report(reporter);
        functionFrameStrict.report(reporter);
        ctrl.replay();
        exercise();
        instance.report(reporter);
        ctrl.verify();
    }
}
