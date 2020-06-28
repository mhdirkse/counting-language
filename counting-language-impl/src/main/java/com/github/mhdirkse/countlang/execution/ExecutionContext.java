package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.StackFrameAccess;

public interface ExecutionContext {

    boolean hasSymbol(String name);

    Object getValue(String name);

    CountlangType getType(String name);

    void putSymbol(String name, Object value);

    void putSymbolInNewFrame(String name, Object value);

    boolean hasFunction(String name);

    FunctionDefinitionStatement getFunction(String name);

    void putFunction(final FunctionDefinitionStatement function);

    void startPreparingNewFrame(StackFrameAccess stackFrameAccess);

    void pushNewFrame();

    void popFrame();

    void output(String result);

    void pushValue(Object value);
    
    Object popValue();
}