package com.github.mhdirkse.countlang.predef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

abstract class ArraySort implements PredefinedFunction {
    private final String name;

    ArraySort(String name) {
        this.name = name;
    }

    @Override
    public FunctionKey getKey() {
        return new FunctionKey(name, CountlangType.arrayOfAny());
    }

    @Override
    public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
        if(arguments.size() != 1) {
            errorHandler.handleParameterCountMismatch(1, arguments.size());
        }
        return CountlangType.arrayOf(arguments.get(0).getSubType());
    }

    @Override
    public Object run(int line, int column, List<Object> args) {
        // Copying is done by CountlangArray.
        List<Comparable<Object>> values = ((CountlangArray) args.get(0)).getMembers();
        Collections.sort(values);
        afterSort(values);
        return new CountlangArray(new ArrayList<>(values));
    }

    abstract void afterSort(List<Comparable<Object>> values);
}
