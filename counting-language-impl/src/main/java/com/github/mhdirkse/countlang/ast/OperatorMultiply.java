package com.github.mhdirkse.countlang.ast;

import java.util.List;

public final class OperatorMultiply extends Operator {
    @Override
    public String getName() {
        return "*";
    }

    @Override
    public Value execute(final List<Value> arguments) {
        int first = arguments.get(0).getValue();
        int second = arguments.get(1).getValue();
        return new Value(first * second);
    }
}
