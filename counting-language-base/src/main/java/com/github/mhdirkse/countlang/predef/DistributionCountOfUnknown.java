package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionCountOfUnknown extends AbstractMemberFunction {
	@SuppressWarnings("unchecked")
	public DistributionCountOfUnknown() {
		super("countOfUnknown", CountlangType.distributionOfAny(), t -> CountlangType.integer());
	}

	@Override
	public BigInteger run(int line, int column, List<Object> value) {
		Distribution distribution = (Distribution) value.get(0);
		return distribution.getCountUnknown();
	}
}
