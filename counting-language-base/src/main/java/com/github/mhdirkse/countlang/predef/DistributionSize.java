package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionSize extends AbstractMemberFunction {
	@SuppressWarnings("unchecked")
	public DistributionSize() {
		super("size", CountlangType.distributionOfAny(), t -> CountlangType.integer());
	}

	@Override
	public BigInteger run(int line, int column, List<Object> values) {
		Distribution distribution = (Distribution) values.get(0);
		return distribution.getTotal();
	}
}
