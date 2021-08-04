package com.github.mhdirkse.countlang.ast;

import java.util.List;

public interface PredefinedFunction extends FunctionDefinition {
    Object run(int line, int column, List<Object> args);
}
