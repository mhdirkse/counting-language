package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangType;

abstract class AbstractDistributionCountOf implements PredefinedFunction {
	private final String name;
	private final CountlangType resultType;

	AbstractDistributionCountOf(String name, CountlangType resultType) {
		this.name = name;
		this.resultType = resultType;
	}

	@Override
	public FunctionKey getKey() {
		return new FunctionKey(name, CountlangType.distributionOfAny());
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 2) {
			errorHandler.handleParameterCountMismatch(2, arguments.size());
			return CountlangType.unknown();
		}
		CountlangType thisArg = arguments.get(0);
		CountlangType arg = arguments.get(1);
		if(! thisArg.isDistribution()) {
			errorHandler.handleParameterTypeMismatch(0, CountlangType.distributionOfAny(), thisArg);
			return CountlangType.unknown();
		}
		if(arg != thisArg.getSubType()) {
			errorHandler.handleParameterTypeMismatch(1, thisArg.getSubType(), arg);
			return CountlangType.unknown();
		}
		return resultType;
	}
}
