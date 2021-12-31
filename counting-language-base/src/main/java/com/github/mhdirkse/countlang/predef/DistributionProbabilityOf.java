package com.github.mhdirkse.countlang.predef;

import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionProbabilityOf extends AbstractMemberFunction {
	private DistributionsStrategy strategy = DistributionsStrategy.notEmpty();

	public DistributionProbabilityOf() {
		super("probabilityOf", CountlangType.distributionOfAny(), t -> CountlangType.fraction(), t -> t.getSubType());
	}

	@Override
	public BigFraction run(int line, int column, List<Object> args) {
		strategy.test(line, column, args);
		Distribution thisArg = (Distribution) args.get(0);
		Object item = args.get(1);
		return new BigFraction(thisArg.getCountOf(item), thisArg.getTotal());
	}
}
