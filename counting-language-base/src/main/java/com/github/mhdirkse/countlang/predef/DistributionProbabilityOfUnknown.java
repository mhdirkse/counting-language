package com.github.mhdirkse.countlang.predef;

import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionProbabilityOfUnknown extends AbstractMemberFunction {
	private DistributionsStrategy strategy = DistributionsStrategy.notEmpty();

	public DistributionProbabilityOfUnknown() {
		super("probabilityOfUnknown", CountlangType.distributionOfAny(), t -> CountlangType.fraction());
	}

	@Override
	public BigFraction run(int line, int column, List<Object> values) {
		strategy.test(line, column, values);
		Distribution distribution = (Distribution) values.get(0);
		return new BigFraction(distribution.getCountUnknown(), distribution.getTotal());
	}
}
