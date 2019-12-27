package com.github.mhdirkse.countlang.ast;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.same;

import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.execution.RunnableFunction;

public class FunctionCallExpressionTest extends EasyMockSupport {
    private static final int FUNCTION_RESULT = 5;
    private static final Integer DUMMY_ARGUMENT = 0;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testHappy() {
        ExecutionContext ctx = strictMock(ExecutionContext.class);
        RunnableFunction function = mock(RunnableFunction.class);
        expect(ctx.hasFunction("myFunction")).andReturn(true);
        expect(ctx.getFunction("myFunction")).andReturn(function);
        expectRunFunction(function, ctx);
        replayAll();
        FunctionCallExpression instance = new FunctionCallExpression(1, 1);
        instance.setFunctionName("myFunction");
        instance.addArgument(new ValueExpression(1, 1, DUMMY_ARGUMENT));
        Object result = instance.calculate(ctx);
        Assert.assertEquals(FUNCTION_RESULT, result);
        verifyAll();
    }

    @SuppressWarnings("unchecked")
    private void expectRunFunction(final RunnableFunction functionMock, ExecutionContext ctxMock) {
        expect(functionMock.runFunction(anyObject(List.class), same(ctxMock)))
        .andReturn(Integer.valueOf(FUNCTION_RESULT));
    }

    @Test
    public void testWhenFunctionNotFoundThenRaiseError() {
        ExecutionContext ctx = strictMock(ExecutionContext.class);
        expect(ctx.hasFunction("myFunction")).andReturn(false);
        replayAll();
        FunctionCallExpression instance = new FunctionCallExpression(1, 1);
        instance.setFunctionName("myFunction");
        instance.addArgument(new ValueExpression(1, 1, DUMMY_ARGUMENT));
        thrown.expect(ProgramException.class);
        thrown.expectMessage("Function not found: myFunction");
        instance.calculate(ctx);
    }
}
