package com.github.mhdirkse.countlang.ast;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramException;

@RunWith(EasyMockRunner.class)
public class ReturnStatementTest {
    @Mock
    private ExecutionContext executionContext;

    @Test(expected = ProgramException.class)
    public void whenReturnStatementExecutedThenProgramRuntimeExceptionThrown() {
        new ReturnStatement(1, 1).execute(executionContext);
    }
}
