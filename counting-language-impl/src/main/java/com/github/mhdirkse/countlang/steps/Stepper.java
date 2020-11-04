package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.execution.FunctionDefinitions;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.SymbolFrameStackExecute;

public interface Stepper {
    boolean hasMoreSteps();
    void step();
    ExecutionPoint getExecutionPoint();
    
    default void run() {
        while(hasMoreSteps() ) {
            step();
        }
    }

    default void runUntil(ExecutionPoint executionPoint) {
        while(hasMoreSteps() && ! getExecutionPoint().equals(executionPoint)) {
            step();
        }
    }

    public static Stepper getInstance(
            final AstNode target, final OutputStrategy outputStrategy, List<FunctionDefinitionStatement> predefinedFunctions) {
        SymbolFrameStackExecute symbolFrameStack = new SymbolFrameStackExecute();
        FunctionDefinitions funDefs = new FunctionDefinitions();
        predefinedFunctions.forEach(f -> funDefs.putFunction(f));
        AstNodeExecutionFactory factory = new AstNodeExecutionFactoryCalculate();
        ExecutionContextCalculate context = new ExecutionContextCalculate(symbolFrameStack, funDefs, outputStrategy);
        StepperImpl result = new StepperImpl(target, context, factory);
        result.init();
        context.setStepperCallback(result);
        return result;
    }
}
