package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Value;

public interface ExecutionContext {

    boolean hasSymbol(String name);

    Value getValue(String name);

    void putSymbol(String name, Value value);

    void pushFrame(StackFrame frame);

    void popFrame();

    void addFunction(FunctionDefinitionStatement definition);

    OutputStrategy getOutputStrategy();

    void setOutputStrategy(OutputStrategy outputStrategy);
}