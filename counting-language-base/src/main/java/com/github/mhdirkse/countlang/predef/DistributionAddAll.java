package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionAddAll implements PredefinedFunction {
	@Override
	public FunctionKey getKey() {
		return new FunctionKey("addAll", CountlangType.distributionOfAny());
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
		Distribution orig = (Distribution) args.get(0);
		Distribution toAdd = (Distribution) args.get(1);
		Distribution.Builder b = new Distribution.Builder();
		addAll(b, orig);
		addAll(b, toAdd);
		return b.build();
	}

	private void addAll(Distribution.Builder result, Distribution toAdd) {
		toAdd.getItemIterator().forEachRemaining(v -> result.add(v, toAdd.getCountOf(v)));
		result.addUnknown(toAdd.getCountUnknown());
	}
}
