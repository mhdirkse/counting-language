package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.execution.DummyValue;

abstract class FunctionDefinitionStatementAnalysis<T> implements AstNodeExecution<T> {
    final FunctionDefinitionStatement funDef;
    AstNodeExecutionState state;
    boolean isBeforeStatements= true;

    FunctionDefinitionStatementAnalysis(FunctionDefinitionStatement funDef) {
        this.funDef = funDef;
    }

    @Override
    public AstNode getAstNode() {
        return funDef;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext<T> context) {
        if(state == AFTER) {
            return null;
        }
        if(!isValid(context)) {
            return null;
        }
        if(isBeforeStatements) {
            return doBeforeStatements(context);
        }
        else {
            return doAfterStatements(context);
        }
    }

    abstract boolean isValid(ExecutionContext<T> context);

    private AstNode doBeforeStatements(ExecutionContext<T> context) {
        state = RUNNING;
        before(context);
        context.pushVariableFrame();
        for(FormalParameter p: funDef.getFormalParameters().getFormalParameters()) {
            context.writeSymbol(p.getName(), getValue(p));
        }
        isBeforeStatements = false;
        return funDef.getStatements();        
    }

    abstract void before(ExecutionContext<T> context);
    abstract T getValue(FormalParameter p);

    private AstNode doAfterStatements(ExecutionContext<T> context) {
        context.popVariableFrame();
        after(context);
        state = AFTER;
        return null;
    }

    abstract void after(ExecutionContext<T> context);

    static class TypeCheck extends FunctionDefinitionStatementAnalysis<CountlangType> {
        private CountlangType returnType = null;

        TypeCheck(final FunctionDefinitionStatement statement) {
            super(statement);
        }

        @Override
        boolean isValid(ExecutionContext<CountlangType> context) {
            if(context.getFunctionDefinitionDepth() >= 1) {
                // TODO: Report nested function definition
                state = AFTER;
                return false;
            }
            if(context.hasFunction(funDef.getName())) {
                // TODO: Report that function already exists
                state = AFTER;
                return false;
            }
            return true;
        }

        @Override
        void before(ExecutionContext<CountlangType> context) {
            context.onFunctionEntered();            
        }

        @Override
        CountlangType getValue(final FormalParameter p) {
            return p.getCountlangType();
        }

        @Override
        void after(ExecutionContext<CountlangType> context) {
            if(returnType == null) {
                // TODO: Report missing return value
                returnType = CountlangType.UNKNOWN;
            }
            funDef.setReturnType(returnType);
            context.onFunctionLeft();            
        }

        @Override
        public boolean handleDescendantResult(CountlangType value, ExecutionContext<CountlangType> context) {
            returnType = value;
            return true;
        }
    }

    static class VarUsage extends FunctionDefinitionStatementAnalysis<DummyValue> {
        VarUsage(FunctionDefinitionStatement statement) {
            super(statement);
        }

        @Override
        public boolean handleDescendantResult(DummyValue value, ExecutionContext<DummyValue> context) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        boolean isValid(ExecutionContext<DummyValue> context) {
            return true;
        }

        @Override
        void before(ExecutionContext<DummyValue> context) {
        }

        @Override
        DummyValue getValue(FormalParameter p) {
            return DummyValue.getInstance();
        }

        @Override
        void after(ExecutionContext<DummyValue> context) {
        }
    }
}
