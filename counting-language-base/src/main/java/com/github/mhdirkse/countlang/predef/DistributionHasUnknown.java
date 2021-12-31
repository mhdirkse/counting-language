package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionHasUnknown extends AbstractMemberFunction {
	public DistributionHasUnknown() {
		super("hasUnknown", CountlangType.distributionOfAny(), t -> CountlangType.bool());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		Distribution thisArg = (Distribution) args.get(0);
		return thisArg.getCountUnknown().compareTo(BigInteger.ZERO) != 0;
	}
}
