package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;

interface ExecutionContext extends StepperCallback {
    Object readSymbol(String symbol, AstNode node);
    void writeSymbol(String symbol, Object value, AstNode node);
    void pushVariableFrame(StackFrameAccess access);
    void popVariableFrame();
    boolean hasFunction(String name);
    void defineFunction(FunctionDefinitionStatement functionDefinitionStatement);
    FunctionDefinitionStatementBase getFunction(String functionName);
    void output(String text);
}
