package com.github.mhdirkse.countlang.predef;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayAdd implements PredefinedFunction {
	private String name = "add";

    @Override
    public FunctionKey getKey() {
        return new FunctionKey(name, CountlangType.arrayOfAny());
    }

    @Override
    public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
        if(arguments.size() != 2) {
            errorHandler.handleParameterCountMismatch(2, arguments.size());
            return CountlangType.unknown();
        }
        if(! arguments.get(0).isArray()) {
        	errorHandler.handleParameterTypeMismatch(0, CountlangType.arrayOfAny(), arguments.get(0));
        	return CountlangType.unknown();
        }
        if(arguments.get(1) != arguments.get(0).getSubType()) {
        	errorHandler.handleParameterTypeMismatch(1, arguments.get(0).getSubType(), arguments.get(1));
        	return CountlangType.unknown();
        }
        return arguments.get(0);
    }

	@SuppressWarnings("unchecked")
	@Override
    public Object run(int line, int column, List<Object> args) {
        // Copying is done by CountlangArray.
        List<Comparable<Object>> values = ((CountlangArray) args.get(0)).getMembers();
        values.add((Comparable<Object>) args.get(1));
        return new CountlangArray(new ArrayList<>(values));
    }
}
