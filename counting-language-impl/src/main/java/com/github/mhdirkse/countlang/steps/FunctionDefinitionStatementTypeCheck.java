package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;

class FunctionDefinitionStatementTypeCheck implements AstNodeExecution<CountlangType> {
    private final FunctionDefinitionStatement funDef;
    private AstNodeExecutionState state;
    boolean isBeforeStatements= true;
    CountlangType returnType = null;

    FunctionDefinitionStatementTypeCheck(FunctionDefinitionStatement funDef) {
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
    public AstNode step(ExecutionContext<CountlangType> context) {
        if(state == AFTER) {
            return null;
        }
        if(context.getFunctionDefinitionDepth() >= 1) {
            // TODO: Report nested function definition
            state = AFTER;
            return null;
        }
        if(isBeforeStatements) {
            return doBeforeStatements(context);
        }
        else {
            return doAfterStatements(context);
        }
    }

    private AstNode doBeforeStatements(ExecutionContext<CountlangType> context) {
        state = RUNNING;
        context.onFunctionEntered();
        if(context.hasFunction(funDef.getName())) {
            // TODO: Report that function already exists
            state = AFTER;
            return null;
        }
        context.pushVariableFrame();
        for(FormalParameter p: funDef.getFormalParameters().getFormalParameters()) {
            context.writeSymbol(p.getName(), p.getCountlangType());
        }
        isBeforeStatements = false;
        return funDef.getStatements();        
    }

    private AstNode doAfterStatements(ExecutionContext<CountlangType> context) {
        context.popVariableFrame();
        if(returnType == null) {
            // TODO: Report missing return value
            returnType = CountlangType.UNKNOWN;
        }
        funDef.setReturnType(returnType);
        context.onFunctionLeft();
        return null;
    }

    @Override
    public boolean handleDescendantResult(CountlangType value) {
        returnType = value;
        return true;
    }    
}
