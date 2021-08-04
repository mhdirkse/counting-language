package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;

public class DistributionCountOf implements PredefinedFunction {

	@Override
	public FunctionKey getKey() {
		return new FunctionKey("countOf", CountlangType.distributionOfAny());
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 2) {
			errorHandler.handleParameterCountMismatch(2, arguments.size());
			return null;
		}
		CountlangType thisArg = arguments.get(0);
		CountlangType arg = arguments.get(1);
		if(! thisArg.isDistribution()) {
			errorHandler.handleParameterTypeMismatch(0, CountlangType.distributionOfAny(), thisArg);
			return null;
		}
		if(arg != thisArg.getSubType()) {
			errorHandler.handleParameterTypeMismatch(1, thisArg.getSubType(), arg);
			return null;
		}
		return CountlangType.integer();
	}

	@Override
	public Object run(List<Object> args) {
		Distribution d = (Distribution) args.get(0);
		return d.getCountOf(args.get(1));
	}
}
