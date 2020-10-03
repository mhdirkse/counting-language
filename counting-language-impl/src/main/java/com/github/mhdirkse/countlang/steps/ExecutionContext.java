package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.utils.AbstractStatusCode;

interface ExecutionContext<T> extends StepperCallback<T> {
    T readSymbol(String symbol);
    void writeSymbol(T value);
    void output(String text);
    void report(AbstractStatusCode statusCode, int line, int column, String... others);
    void onStatement(AstNode node);
    void onReturn(AstNode node);
    void onSwitchOpened();
    void onSwitchClosed();
    void onBranchOpened();
    void onBranchClosed();    
}
