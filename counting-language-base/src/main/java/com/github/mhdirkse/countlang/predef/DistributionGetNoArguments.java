package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangType;

abstract class DistributionGetNoArguments implements PredefinedFunction {
	private String name;

	DistributionGetNoArguments(String name) {
		this.name = name;
	}

	@Override
	public FunctionKey getKey() {
		return new FunctionKey(name, CountlangType.distributionOfAny());
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 1) {
			errorHandler.handleParameterCountMismatch(1, arguments.size());
			return CountlangType.unknown();
		}
		return CountlangType.integer();
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		return getResult(((Distribution) args.get(0)));
	}

	abstract BigInteger getResult(Distribution distribution);
}
