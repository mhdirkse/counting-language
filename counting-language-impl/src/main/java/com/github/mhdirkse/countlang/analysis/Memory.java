package com.github.mhdirkse.countlang.analysis;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;

interface Memory extends BlockListener {
    List<VariableErrorEvent> getVariableErrorEvents();
    void pushScope(Scope scope);
    Scope popScope();
    CountlangType read(String name, int line, int column, CodeBlock codeBlock);
    void write(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock);
    void addParameter(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock);
}
