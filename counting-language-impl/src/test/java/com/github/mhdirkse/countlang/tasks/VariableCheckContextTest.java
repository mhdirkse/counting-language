package com.github.mhdirkse.countlang.tasks;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;

import java.util.Arrays;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class VariableCheckContextTest {
    private static final String NAME = "xyz";
    private static final int LINE = 2;
    private static final int COLUMN = 3;

    private VariableCheckFrame topLevelFrameStrict;
    private VariableCheckFrame functionFrameStrict;
    private IMocksControl ctrl;
    private VariableCheckFrame topLevelFrameNice;
    private VariableCheckFrame functionFrameNice;
    private IMocksControl nice;
    private StatusReporter reporter;
    private List<VariableCheckFrame> mockFrames;
    private int mockFrameIndex;
    private VariableCheckContext instance;

    @Before
    public void setUp() {
        ctrl = createStrictControl();
        topLevelFrameStrict = ctrl.createMock(VariableCheckFrame.class);
        functionFrameStrict = ctrl.createMock(VariableCheckFrame.class);
        nice = createNiceControl();
        topLevelFrameNice = nice.createMock(VariableCheckFrame.class);
        functionFrameNice = nice.createMock(VariableCheckFrame.class);
        reporter = createNiceMock(StatusReporter.class);
        mockFrameIndex = 0;
        instance = new VariableCheckContext(this::nextFrame);
    }

    VariableCheckFrame nextFrame() {
        return mockFrames.get(mockFrameIndex++);
    }

    @Test
    public void testDelegatesToFirstFrame() {
        mockFrames = Arrays.asList(topLevelFrameStrict, functionFrameStrict);
        topLevelFrameStrict.define(anyObject(String.class), anyInt(), anyInt());
        functionFrameStrict.define(anyObject(String.class), anyInt(), anyInt());
        topLevelFrameStrict.define(anyObject(String.class), anyInt(), anyInt());
        ctrl.replay();
        exercise();
        ctrl.verify();
    }

    private void exercise() {
        instance.pushNewFrame();
        instance.define(NAME, LINE, COLUMN);
        instance.pushNewFrame();
        instance.define(NAME, LINE, COLUMN);
        instance.popFrame();
        instance.define(NAME, LINE, COLUMN);
        instance.popFrame();
    }

    @Test
    public void testWhenMultipleFramesCreatedThenMultipleReported() {
        mockFrames = Arrays.asList(topLevelFrameNice, functionFrameNice);
        topLevelFrameNice.report(reporter);
        functionFrameNice.report(reporter);
        nice.replay();
        exercise();
        instance.report(reporter);
        nice.verify();
    }
}
