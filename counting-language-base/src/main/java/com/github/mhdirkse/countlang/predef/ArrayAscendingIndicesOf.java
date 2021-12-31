package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayAscendingIndicesOf implements PredefinedFunction {
	@Override
	public FunctionKey getKey() {
		return new FunctionKey("ascendingIndicesOf", CountlangType.arrayOfAny());
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 2) {
			errorHandler.handleParameterCountMismatch(2, arguments.size());
			return CountlangType.unknown();
		}
		CountlangType thisArg = arguments.get(0);
		CountlangType toFind = arguments.get(1);
		if(toFind != thisArg.getSubType()) {
			errorHandler.handleParameterTypeMismatch(1, thisArg.getSubType(), toFind);
			return CountlangType.unknown();
		}
		return CountlangType.arrayOf(CountlangType.integer());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		Object toFind = args.get(1);
		List<Object> members = thisArg.getAll();
		List<BigInteger> result = new ArrayList<>();
		for(int i = 0; i < members.size(); ++i) {
			Object actual = members.get(i);
			if(actual.equals(toFind)) {
				long longIndex = i + 1;
				result.add(BigInteger.valueOf(longIndex));
			}
		}
		return result;
	}
}
