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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ExecutionContextImpl;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.ProgramRuntimeException;
import com.github.mhdirkse.countlang.execution.StackFrame;

public class FunctionDefinitionStatementTest implements OutputStrategy {
    private List<String> outputs;
    private List<String> errors;

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
        ctx.pushFrame(anyObject(StackFrame.class));
        expect(ctx.hasSymbol(functionCreator.getFormalParameter())).andReturn(true);
        expect(ctx.getValue(functionCreator.getFormalParameter())).andReturn(new Value(functionCreator.getParameterValue()));
        ctx.popFrame();
        replay(ctx);
        Value result = instance.runFunction(Arrays.asList(functionCreator.getActualParameter()), ctx);
        Assert.assertEquals(functionCreator.getExpectedResult(), result.getValue());
        verify(ctx);
    }

    @Test
    public void testFunctionGivesCorrectResult() {
        FunctionCreatorValidFunction functionCreator = new FunctionCreatorValidFunction();
        FunctionDefinitionStatement instance = functionCreator.createFunction();
        ExecutionContext ctx = new ExecutionContextImpl(this);
        Value result = instance.runFunction(Arrays.asList(functionCreator.getActualParameter()), ctx);
        Assert.assertEquals(functionCreator.getExpectedResult(), result.getValue());
        Assert.assertEquals(0, outputs.size());
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testWhenStatementWithoutEffectRaiseError() {
        FunctionCreatorStatementWithoutEffect functionCreator = new FunctionCreatorStatementWithoutEffect();
        FunctionDefinitionStatement instance = functionCreator.createFunction();
        ExecutionContext ctx = new ExecutionContextImpl(this);
        thrown.expect(ProgramRuntimeException.class);
        thrown.expectMessage("Statement has no effect");
        instance.runFunction(Arrays.asList(functionCreator.getActualParameter()), ctx);        
    }

    @Test
    public void testWhenNoReturnStatementRaiseError() {
        FunctionCreatorNoReturn functionCreator = new FunctionCreatorNoReturn();
        FunctionDefinitionStatement instance = functionCreator.createFunction();
        ExecutionContext ctx = new ExecutionContextImpl(this);
        thrown.expect(ProgramRuntimeException.class);
        thrown.expectMessage("No return statement in function");
        instance.runFunction(Arrays.asList(functionCreator.getActualParameter()), ctx);        
    }

    @Test
    public void testWhenParameterCountMismatchRaiseError() {
        FunctionCreatorFormalParameterOmitted functionCreator = new FunctionCreatorFormalParameterOmitted();
        FunctionDefinitionStatement instance = functionCreator.createFunction();
        ExecutionContext ctx = new ExecutionContextImpl(this);
        thrown.expect(ProgramRuntimeException.class);
        thrown.expectMessage("In function call expected 0 arguments, got 1");
        instance.runFunction(Arrays.asList(functionCreator.getActualParameter()), ctx);        
    }

    private static abstract class FunctionCreatorBase {
        static final int ADDED_VALUE = 5;
        private static final int VALUE_OF_X = 3;
        private static final String FORMAL_PARAMETER = "x";

        FunctionDefinitionStatement instance = new FunctionDefinitionStatement(1, 1);

        FunctionDefinitionStatement createFunction() {
            instance.setName("dummy");
            handleParameter();
            instance.addStatement(getStatement());
            handleExtraStatement();
            return instance;
        }

        abstract void handleParameter();

        void addTheParameter() {
            instance.addFormalParameter(FORMAL_PARAMETER);
        }

        abstract Statement getStatement();

        Statement getReturnStatement() {
            CompositeExpression ex1 = getStatementExpression();
            ReturnStatement result = new ReturnStatement(1, 1);
            result.setExpression(ex1);
            return result;
        }

        Statement getPrintStatement() {
            CompositeExpression ex1 = getStatementExpression();
            PrintStatement result = new PrintStatement(1, 1);
            result.setExpression(ex1);
            return result;
        }

        CompositeExpression getStatementExpression() {
            ValueExpression ex11 = new ValueExpression(1, 1);
            ex11.setValue(new Value(ADDED_VALUE));
            SymbolExpression ex12 = new SymbolExpression(1, 1);
            ex12.setSymbol(new Symbol(FORMAL_PARAMETER));
            CompositeExpression ex1 = new CompositeExpression(1, 1);
            ex1.setOperator(new OperatorAdd());
            ex1.addSubExpression(ex11);
            ex1.addSubExpression(ex12);
            return ex1;
        }

        Expression getActualParameter() {
            ValueExpression result = new ValueExpression(1, 1);
            result.setValue(new Value(VALUE_OF_X));
            return result;
        }

        String getFormalParameter() {
            return FORMAL_PARAMETER;
        }

        int getParameterValue() {
            return VALUE_OF_X;
        }

        int getExpectedResult() {
            return ADDED_VALUE + VALUE_OF_X;            
        }

        abstract void handleExtraStatement();
    }

    private static class FunctionCreatorValidFunction extends FunctionCreatorBase {
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
        }
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
            ValueExpression ex = new ValueExpression(1, 1);
            ex.setValue(new Value(ADDED_VALUE));
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
