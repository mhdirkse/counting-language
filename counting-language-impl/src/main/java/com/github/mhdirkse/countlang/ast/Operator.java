package com.github.mhdirkse.countlang.ast;

import java.util.List;

public abstract class Operator {
    public abstract String getName();
    public abstract Value execute(final List<Value> arguments);
}
