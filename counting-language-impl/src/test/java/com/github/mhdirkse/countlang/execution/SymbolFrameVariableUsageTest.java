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

    private void openBranch() {
        instance.onSwitchOpened();
        instance.onBranchOpened();
    }

    private void closeBranch() {
        instance.onBranchClosed();
        instance.onSwitchClosed();
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

    @Test(expected = IllegalStateException.class)
    public void whenNewSymbolWrittenInBranchThenerror() {
        EasyMock.replay(handler);
        openBranch();
        instance.write(SYMBOL, DUMMY, 1, 1);
    }

    @Test
    public void whenSwitchClosedThenWriteNewSymbolIsNotAnError() {
        handler.variableNotUsed(SYMBOL, 1, 1);
        EasyMock.replay(handler);
        openBranch();
        closeBranch();
        instance.write(SYMBOL, DUMMY, 1, 1);
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }

    @Test
    public void whenRewrittenInBranchThenInitialWriteNotReportedAsUnused() {
        EasyMock.replay(handler);
        instance.write(SYMBOL, DUMMY, 1, 1);
        openBranch();
        instance.write(SYMBOL, DUMMY, 2, 3);
        closeBranch();
        instance.read(SYMBOL, 3, 4);
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }

    @Test
    public void whenInBranchVariableWrittenTwiceThenWriteInBranchUnused() {
        handler.variableNotUsed(SYMBOL, 2, 3);
        EasyMock.replay(handler);
        instance.write(SYMBOL, DUMMY, 1, 1);
        openBranch();
        instance.write(SYMBOL, DUMMY, 2, 3);
        instance.write(SYMBOL, DUMMY, 3, 4);
        closeBranch();
        instance.read(SYMBOL, 5, 6);
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }

    @Test
    public void whenWriteBeforeBranchReadInBranchThenNoError() {
        EasyMock.replay(handler);
        instance.write(SYMBOL, DUMMY, 1, 1);
        openBranch();
        instance.read(SYMBOL, 2, 3);
        closeBranch();
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }

    @Test
    public void whenFirstWriteInBranchReadThenSecondWriteMayBeReadOutsideBranch() {
        EasyMock.replay(handler);
        instance.write(SYMBOL, DUMMY, 1, 1);
        openBranch();
        instance.write(SYMBOL, DUMMY, 2, 3);
        instance.read(SYMBOL, 3, 4);
        instance.write(SYMBOL, DUMMY, 4, 5);
        closeBranch();
        instance.read(SYMBOL, 5, 6);
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }

    @Test
    public void whenInNestedBranchVariableWrittenTwiceWithoutReadThenNotUsed() {
        handler.variableNotUsed(SYMBOL, 2, 3);
        EasyMock.replay(handler);
        instance.write(SYMBOL, DUMMY, 1, 1);
        openBranch();
        openBranch();
        instance.write(SYMBOL, DUMMY, 2, 3);
        instance.write(SYMBOL, DUMMY, 3, 4);
        closeBranch();
        instance.read(SYMBOL, 5, 6);
        closeBranch();
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }

    @Test
    public void whenFirstWriteInNestedBranchReadThenSecondWriteMayBeReadOutsideBranch() {
        EasyMock.replay(handler);
        instance.write(SYMBOL, DUMMY, 1, 1);
        openBranch();
        openBranch();
        instance.write(SYMBOL, DUMMY, 2, 3);
        instance.read(SYMBOL, 3, 4);
        instance.write(SYMBOL, DUMMY, 4, 5);
        closeBranch();
        closeBranch();
        instance.read(SYMBOL, 5, 6);
        instance.listEvents(handler);
        EasyMock.verify(handler);
    }
}
