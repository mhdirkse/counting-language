package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.ast.ProgramException;

public class DistributionIntSum implements PredefinedFunction {

	@Override
	public FunctionKey getKey() {
		return new FunctionKey("sum", CountlangType.distributionOf(CountlangType.integer()));
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 1) {
			errorHandler.handleParameterCountMismatch(1, arguments.size());
			return null;
		}
		CountlangType thisArg = arguments.get(0);
		if(thisArg != CountlangType.distributionOf(CountlangType.integer())) {
			errorHandler.handleParameterTypeMismatch(1, CountlangType.distributionOf(CountlangType.integer()), thisArg);
			return null;
		}
		return CountlangType.integer();
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		Distribution d = (Distribution) args.get(0);
		if(d.getCountUnknown().compareTo(BigInteger.ZERO) != 0) {
			throw new ProgramException(line, column, "Cannot execute sum() on distribution that has unknown");
		}
		BigInteger result = BigInteger.ZERO;
		Iterator<Object> it = d.getItemIterator();
		while(it.hasNext()) {
			Object item = it.next();
			BigInteger count = d.getCountOf(item);
			BigInteger toAdd = ((BigInteger) item).multiply(count);
			result = result.add(toAdd);
		}
		return result;
	}
}
