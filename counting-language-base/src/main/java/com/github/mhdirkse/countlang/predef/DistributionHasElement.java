package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionHasElement extends AbstractMemberFunction {
	public DistributionHasElement() {
		super("hasElement", CountlangType.distributionOfAny(), t -> CountlangType.bool(), t -> t.getSubType());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		Distribution thisArg = (Distribution) args.get(0);
		Object item = args.get(1);
		return thisArg.getCountOf(item).compareTo(BigInteger.ZERO) != 0;
	}
}
