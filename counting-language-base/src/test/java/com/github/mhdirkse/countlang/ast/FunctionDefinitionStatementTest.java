package com.github.mhdirkse.countlang.ast;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.strictMock;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.mhdirkse.countlang.ast.TestFunctionDefinitions.*;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ExecutionContextImpl;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.ProgramException;

public class FunctionDefinitionStatementTest implements OutputStrategy {
    private List<String> outputs;
    private List<String> errors;

    /**
     * This rule does not cooperate well with EclEmma. When this rule handles
     * a thrown exception, then EclEmma can not correctly show the coverage. See
     * {@linktourl https://www.eclemma.org/faq.html#trouble02}.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        FunctionCreatorValidFunction functionCreator = new FunctionCreatorValidFunction();
        FunctionDefinitionStatement instance = functionCreator.createFunction();
        ExecutionContext ctx = strictMock(ExecutionContext.class);
        ctx.startPreparingNewFrame();
        ctx.putSymbolInNewFrame(
        		TestFunctionDefinitions.FORMAL_PARAMETER, TestFunctionDefinitions.VALUE_OF_X_AS_VALUE);
        ctx.pushNewFrame();
        expect(ctx.hasSymbol(functionCreator.getFormalParameter())).andReturn(true);
        expect(ctx.getValue(functionCreator.getFormalParameter())).andReturn(Integer.valueOf(functionCreator.getParameterValue()));
        ctx.popFrame();
        replay(ctx);
        Object result = instance.runFunction(Arrays.asList(functionCreator.getActualParameter()), ctx);
        Assert.assertEquals(functionCreator.getExpectedResult(), result);
        verify(ctx);
    }

    @Test
    public void testFunctionGivesCorrectResult() {
        FunctionCreatorValidFunction functionCreator = new FunctionCreatorValidFunction();
        FunctionDefinitionStatement instance = functionCreator.createFunction();
        ExecutionContext ctx = new ExecutionContextImpl(this);
        Object result = instance.runFunction(Arrays.asList(functionCreator.getActualParameter()), ctx);
        Assert.assertEquals(functionCreator.getExpectedResult(), result);
        Assert.assertEquals(0, outputs.size());
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testWhenStatementWithoutEffectRaiseError() {
        FunctionCreatorStatementWithoutEffect functionCreator = new FunctionCreatorStatementWithoutEffect();
        FunctionDefinitionStatement instance = functionCreator.createFunction();
        ExecutionContext ctx = new ExecutionContextImpl(this);
        thrown.expect(ProgramException.class);
        thrown.expectMessage("Statement has no effect");
        instance.runFunction(Arrays.asList(functionCreator.getActualParameter()), ctx);        
    }

    @Test
    public void testWhenNoReturnStatementRaiseError() {
        FunctionCreatorNoReturn functionCreator = new FunctionCreatorNoReturn();
        FunctionDefinitionStatement instance = functionCreator.createFunction();
        ExecutionContext ctx = new ExecutionContextImpl(this);
        thrown.expect(ProgramException.class);
        thrown.expectMessage("No return statement in function");
        instance.runFunction(Arrays.asList(functionCreator.getActualParameter()), ctx);        
    }

    @Test
    public void testWhenParameterCountMismatchRaiseError() {
        FunctionCreatorFormalParameterOmitted functionCreator = new FunctionCreatorFormalParameterOmitted();
        FunctionDefinitionStatement instance = functionCreator.createFunction();
        ExecutionContext ctx = new ExecutionContextImpl(this);
        thrown.expect(ProgramException.class);
        thrown.expectMessage("In function call expected 0 arguments, got 1");
        instance.runFunction(Arrays.asList(functionCreator.getActualParameter()), ctx);        
    }

    private static class FunctionCreatorStatementWithoutEffect extends FunctionCreatorBase {
        @Override
        void handleParameter() {
            addTheParameter();
        }

        @Override
        Statement getStatement() {
            return getReturnStatement();
        }

        @Override
        void handleExtraStatement() {
            ValueExpression ex = new ValueExpression(1, 1, Integer.valueOf(TestFunctionDefinitions.ADDED_VALUE));
            PrintStatement statement = new PrintStatement(1, 1);
            statement.setExpression(ex);
            instance.addStatement(statement);
        }
    }

    private static class FunctionCreatorNoReturn extends FunctionCreatorBase {
        @Override
        void handleParameter() {
            addTheParameter();
        }

        @Override
        Statement getStatement() {
            return getPrintStatement();
        }

        @Override
        void handleExtraStatement() {
        }
    }

    private static class FunctionCreatorFormalParameterOmitted extends FunctionCreatorBase {
        @Override
        void handleParameter() {
        }

        @Override
        Statement getStatement() {
            return getReturnStatement();
        }

        @Override
        void handleExtraStatement() {
        }
    }
}
