package com.github.mhdirkse.countlang.predef;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayAddAll implements PredefinedFunction {
	@Override
	public FunctionKey getKey() {
		return new FunctionKey("addAll", CountlangType.arrayOfAny());
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 2) {
			errorHandler.handleParameterCountMismatch(2, arguments.size());
			return CountlangType.unknown();
		}
		CountlangType thisArg = arguments.get(0);
		CountlangType addArg = arguments.get(1);
		if(addArg != thisArg) {
			errorHandler.handleParameterTypeMismatch(1, thisArg, addArg);
			return CountlangType.unknown();
		}
		return thisArg;
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		CountlangArray addArg = (CountlangArray) args.get(1);
		List<Object> elements = new ArrayList<>(thisArg.getAll());
		elements.addAll(addArg.getAll());
		return new CountlangArray(elements);
	}
}
