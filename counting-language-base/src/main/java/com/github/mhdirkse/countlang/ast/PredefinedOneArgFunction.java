package com.github.mhdirkse.countlang.ast;

public interface PredefinedOneArgFunction extends FunctionDefinition {
    Object run(Object arg);
}
