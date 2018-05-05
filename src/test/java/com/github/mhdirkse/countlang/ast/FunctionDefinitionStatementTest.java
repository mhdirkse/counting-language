package com.github.mhdirkse.countlang.ast;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.strictMock;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ExecutionContextImpl;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.StackFrame;

public class FunctionDefinitionStatementTest implements OutputStrategy {
    private static final int ADDED_VALUE = 5;
    private static final int VALUE_OF_X = 3;

    private List<String> outputs;
    private List<String> errors;

    @Override
    public void output(String s) {
        outputs.add(s);
    }

    @Override
    public void error(String s) {
        errors.add(s);
    }

    @Before
    public void setUp() {
        outputs = new ArrayList<>();
        errors = new ArrayList<>();
    }

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

    @Test
    public void testFunctionGivesCorrectResult() {
        FunctionDefinitionStatement instance = createInstance();
        ExecutionContext ctx = new ExecutionContextImpl(this);
        Value result = instance.runFunction(Arrays.asList(getActualParameter()), ctx);
        Assert.assertEquals(ADDED_VALUE + VALUE_OF_X, result.getValue());
        Assert.assertEquals(0, outputs.size());
        Assert.assertEquals(0, errors.size());
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
