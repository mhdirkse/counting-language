package com.github.mhdirkse.countlang.predef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayReverse implements PredefinedFunction {
	@Override
	public FunctionKey getKey() {
		return new FunctionKey("reverse", CountlangType.arrayOfAny());
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 1) {
			errorHandler.handleParameterCountMismatch(1, arguments.size());
			return CountlangType.unknown();
		}
		return arguments.get(0);
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray arg = (CountlangArray) args.get(0);
		List<Object> newMembers = new ArrayList<Object>(arg.getMembers());
		Collections.reverse(newMembers);
		return new CountlangArray(newMembers);
	}

}
