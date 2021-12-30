package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangType;

abstract class AbstractDistributionNoArguments implements PredefinedFunction {
	private final String name;
	private final CountlangType resultType;

	AbstractDistributionNoArguments(String name, CountlangType resultType) {
		this.name = name;
		this.resultType = resultType;
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
		return resultType;
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		return getResult(line, column, (Distribution) args.get(0));
	}

	abstract Object getResult(int line, int column, Distribution distribution);
}
