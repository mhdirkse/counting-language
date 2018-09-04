package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.ast.Operator.OperatorAdd;
import com.github.mhdirkse.countlang.execution.Symbol;
import com.github.mhdirkse.countlang.execution.Value;

public final class TestFunctionDefinitions {

    private TestFunctionDefinitions() {        
    }

    public static FunctionDefinitionStatement createTestFunction() {
        return new FunctionCreatorValidFunction().createFunction();
    }

    static abstract class FunctionCreatorBase {
        static final int ADDED_VALUE = 5;
        private static final int VALUE_OF_X = 3;
        private static final String FORMAL_PARAMETER = "x";

        FunctionDefinitionStatement instance = new FunctionDefinitionStatement(1, 1);

        FunctionDefinitionStatement createFunction() {
            instance.setName("testFunction");
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
            ex1.setOperator(new OperatorAdd(1, 1));
            ex1.addSubExpression(ex11);
            ex1.addSubExpression(ex12);
            return ex1;
        }

        ExpressionNode getActualParameter() {
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

    static class FunctionCreatorValidFunction extends FunctionCreatorBase {
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
}
