package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayUnsort implements PredefinedFunction {
	private final String name = "unsort";

	@Override
	public FunctionKey getKey() {
		return new FunctionKey(name, CountlangType.arrayOfAny());
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 1) {
			errorHandler.handleParameterCountMismatch(1, arguments.size());
			return CountlangType.unknown();
		}
		CountlangType thisArgument = arguments.get(0);
		if(! thisArgument.isArray()) {
			errorHandler.handleParameterTypeMismatch(1, CountlangType.arrayOfAny(), thisArgument);
			return CountlangType.unknown();
		}
		return CountlangType.distributionOf(thisArgument.getSubType());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		Distribution.Builder b = new Distribution.Builder();
		thisArg.getMembers().forEach(m -> b.add(m));
		return b.build();
	}
}
