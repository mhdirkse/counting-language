package com.github.mhdirkse.countlang.ast;

import org.junit.Test;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.StackFrame;

import org.junit.Assert;

import static org.easymock.EasyMock.*;

import java.util.Arrays;

public class FunctionDefinitionStatementTest {
    private static final int ADDED_VALUE = 5;
    private static final int VALUE_OF_X = 3;

    @Test
    public void testFunctionIsExecutedWithItsOwnStackFrame() {
        FunctionDefinitionStatement instance = createInstance();
        ExecutionContext ctx = strictMock(ExecutionContext.class);
        ctx.pushFrame(anyObject(StackFrame.class));
        expect(ctx.hasSymbol("x")).andReturn(true);
        expect(ctx.getValue("x")).andReturn(new Value(VALUE_OF_X));
        ctx.popFrame();
        replay(ctx);
        Value result = instance.runFunction(Arrays.asList(getActualParameter()), ctx);
        Assert.assertEquals(ADDED_VALUE + VALUE_OF_X, result.getValue());
        verify(ctx);
    }

    private FunctionDefinitionStatement createInstance() {
        FunctionDefinitionStatement instance = new FunctionDefinitionStatement(1, 1);
        instance.setName("dummy");
        instance.addFormalParameter("x");
        instance.addStatement(getStatement());
        return instance;
    }

    private Statement getStatement() {
        ValueExpression ex11 = new ValueExpression(1, 1);
        ex11.setValue(new Value(ADDED_VALUE));
        SymbolExpression ex12 = new SymbolExpression(1, 1);
        ex12.setSymbol(new Symbol("x"));
        CompositeExpression ex1 = new CompositeExpression(1, 1);
        ex1.setOperator(new OperatorAdd());
        ex1.addSubExpression(ex11);
        ex1.addSubExpression(ex12);
        ReturnStatement result = new ReturnStatement(1, 1);
        result.setExpression(ex1);
        return result;
    }

    private Expression getActualParameter() {
        ValueExpression result = new ValueExpression(1, 1);
        result.setValue(new Value(VALUE_OF_X));
        return result;
    }
}
