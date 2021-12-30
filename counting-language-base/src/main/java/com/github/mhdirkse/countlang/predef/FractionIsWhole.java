package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangType;

public class FractionIsWhole implements PredefinedFunction {
	@Override
	public FunctionKey getKey() {
		return new FunctionKey("isWhole");
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
		return CountlangType.bool();
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		BigFraction arg = (BigFraction) args.get(0);
		return arg.getDenominator().equals(BigInteger.ONE);
	}
}
