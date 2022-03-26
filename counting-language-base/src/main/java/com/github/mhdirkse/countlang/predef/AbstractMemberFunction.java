package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

abstract class AbstractMemberFunction implements PredefinedFunction {
	private final String name;
	private final CountlangType thisType;
	private final Function<CountlangType, CountlangType> returnType;
	private final List<Function<CountlangType, CountlangType>> argTypes;

	@SafeVarargs
	AbstractMemberFunction(final String name, final CountlangType thisType, final Function<CountlangType, CountlangType> returnType, Function<CountlangType, CountlangType> ...otherArguments) {
		this.name = name;
		this.thisType = thisType;
		this.returnType = returnType;
		this.argTypes = Arrays.asList(otherArguments);
	}

	@Override
	public FunctionKey getKey() {
		return new FunctionKey(name, thisType);
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != (argTypes.size() + 1)) {
			errorHandler.handleParameterCountMismatch(argTypes.size() + 1, arguments.size());
			return CountlangType.unknown();
		}
		for(int i = 1; i < arguments.size(); ++i) {
			// Do not calculate expectedType from thisType, because that one may contain a wildcard.
			// arguments.get(0) is of a correct type because it is matched through the key.
			CountlangType expectedType = argTypes.get(i-1).apply(arguments.get(0));
			CountlangType actualType = arguments.get(i);
			if(actualType != expectedType) {
				errorHandler.handleParameterTypeMismatch(i, expectedType, actualType);
				return CountlangType.unknown();
			}
		}
		return returnType.apply(arguments.get(0));
	}

	void checkArrayIndex(int line, int column, CountlangArray a, BigInteger countlangIndex) {
		if((countlangIndex.compareTo(BigInteger.ONE) < 0) || (countlangIndex.compareTo(BigInteger.valueOf(a.size())) > 0)) {
			throw new ProgramException(line, column, String.format("array<?>.%s(): Index %s out of bounds", name, countlangIndex.toString()));
		}
	}
}
