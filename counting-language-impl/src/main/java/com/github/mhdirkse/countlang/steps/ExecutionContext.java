package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.utils.AbstractStatusCode;

interface ExecutionContext<T> extends StepperCallback<T> {
    T readSymbol(String symbol);
    void writeSymbol(String symbol, T value);
    void pushVariableFrame();
    void popVariableFrame();
    boolean hasFunction(String name);
    void defineFunction(FunctionDefinitionStatement functionDefinitionStatement);
    FunctionDefinitionStatement getFunction(String functionName);
    void output(String text);
    void report(AbstractStatusCode statusCode, int line, int column, String... others);
    void onStatement(AstNode node);
    void onReturn(AstNode node);
    void onSwitchOpened();
    void onSwitchClosed();
    void onBranchOpened();
    void onBranchClosed();
    void onFunctionEntered();
    void onFunctionLeft();
    int getFunctionDefinitionDepth();
}
