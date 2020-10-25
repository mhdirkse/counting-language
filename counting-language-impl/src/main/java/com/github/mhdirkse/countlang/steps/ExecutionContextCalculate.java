package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.execution.FunctionDefinitions;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;
import com.github.mhdirkse.countlang.execution.SymbolFrameStackExecute;

class ExecutionContextCalculate implements ExecutionContext {
    private final SymbolFrameStackExecute symbolFrame;
    private StepperCallback stepperCallback;
    private final FunctionDefinitions funDefs;
    private final OutputStrategy outputStrategy;

    ExecutionContextCalculate(
            final SymbolFrameStackExecute symbolFrame,
            final FunctionDefinitions funDefs,
            final OutputStrategy outputStrategy) {
        this.symbolFrame = symbolFrame;
        this.funDefs = funDefs;
        this.outputStrategy = outputStrategy;
    }

    void setStepperCallback(final StepperCallback stepperCallback) {
        this.stepperCallback = stepperCallback;
    }

    @Override
    public Object onResult(Object value) {
        return stepperCallback.onResult(value);
    }

    @Override
    public Object readSymbol(String symbol, AstNode node) {
        return symbolFrame.read(symbol, node.getLine(), node.getColumn());
    }

    @Override
    public void writeSymbol(String symbol, Object value, AstNode node) {
        symbolFrame.write(symbol, value, node.getLine(), node.getColumn());
        
    }

    @Override
    public void pushVariableFrame(final StackFrameAccess access) {
        symbolFrame.pushFrame(access);
    }

    @Override
    public void popVariableFrame() {
        symbolFrame.popFrame();
    }

    @Override
    public boolean hasFunction(String name) {
        return funDefs.hasFunction(name);
    }

    @Override
    public void defineFunction(FunctionDefinitionStatement functionDefinitionStatement) {
        funDefs.putFunction(functionDefinitionStatement);
    }

    @Override
    public FunctionDefinitionStatement getFunction(String functionName) {
        return funDefs.getFunction(functionName);
    }

    @Override
    public void output(String text) {
        outputStrategy.output(text);
    }
}
