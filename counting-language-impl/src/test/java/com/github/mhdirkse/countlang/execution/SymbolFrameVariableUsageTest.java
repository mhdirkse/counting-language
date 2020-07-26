package com.github.mhdirkse.countlang.execution;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.ProgramException;

@RunWith(EasyMockRunner.class)
public class SymbolFrameVariableUsageTest {
    private static final StackFrameAccess DUMMY_STACK_FRAME_ACCESS = StackFrameAccess.SHOW_PARENT;
    private static final String SYMBOL = "x";
    private static final DummyValue DUMMY = DummyValue.getInstance();

    @Mock
    private VariableUsageEventHandler handler;

    private SymbolFrameVariableUsage instance;

    @Before
    public void setUp() {
        instance = new SymbolFrameVariableUsage(DUMMY_STACK_FRAME_ACCESS);
    }

    @Test
    public void whenSymbolWrittenOnceAndReadThenSymbolUsed() {
        EasyMock.replay(handler);
        instance.write(SYMBOL, DUMMY, 1, 1);
        instance.read(SYMBOL, 2, 3);
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }

    @Test(expected = ProgramException.class)
    public void whenUnknownSystemReadThenError() {
        instance.read(SYMBOL, 2, 3);
    }

    @Test
    public void whenSymbolOnlyWrittenThenNotUsed() {
        handler.variableNotUsed(SYMBOL, 2, 3);
        EasyMock.replay(handler);
        instance.write(SYMBOL, DUMMY, 2, 3);
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }

    @Test
    public void whenSymbolRewrittenAndNotReadAgainThenNotUsed() {
        handler.variableNotUsed(SYMBOL, 3, 4);
        EasyMock.replay(handler);
        instance.write(SYMBOL, DUMMY, 1, 1);
        instance.read(SYMBOL, 2, 3);
        instance.write(SYMBOL, DUMMY, 3, 4);
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }

    @Test
    public void whenSymbolWrittenTwiceAndReadThenFirstWriteNotUsed() {
        handler.variableNotUsed(SYMBOL, 1, 1);
        EasyMock.replay(handler);
        instance.write(SYMBOL, DUMMY, 1, 1);
        instance.write(SYMBOL, DUMMY, 2, 3);
        instance.read(SYMBOL, 3, 4);
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }
}
