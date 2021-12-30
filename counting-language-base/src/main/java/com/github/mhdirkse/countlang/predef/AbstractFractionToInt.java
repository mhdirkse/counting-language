package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangType;

abstract class AbstractFractionToInt implements PredefinedFunction {
	private final String name;

	AbstractFractionToInt(final String name) {
		this.name = name;
	}

	@Override
	public FunctionKey getKey() {
		return new FunctionKey(name);
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 1) {
			errorHandler.handleParameterCountMismatch(1, arguments.size());
			return CountlangType.unknown();
		}
		if(arguments.get(0) != CountlangType.fraction()) {
			errorHandler.handleParameterTypeMismatch(0, CountlangType.fraction(), arguments.get(0));
			return CountlangType.unknown();
		}
		return CountlangType.integer();
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		return getResult((BigFraction) args.get(0));
	}

	abstract BigInteger getResult(BigFraction arg);
}
