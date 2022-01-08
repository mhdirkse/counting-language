package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionProbabilityOfSet extends AbstractMemberFunction {
	private DistributionsStrategy strategy;

	public DistributionProbabilityOfSet() {
		super("probabilityOfAll", CountlangType.distributionOfAny(), t -> CountlangType.fraction(), t -> t);
		strategy = DistributionsStrategy.of(
				DistributionsStrategy.notEmpty(),
				DistributionsStrategy.otherIsSet("probabilityOfAll"));
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		strategy.test(line, column, args);
		BigInteger count = BigInteger.ZERO;
		Distribution thisArg = (Distribution) args.get(0);
		Distribution other = (Distribution) args.get(1);
		for(Iterator<Object> it = other.getItemIterator(); it.hasNext(); ) {
			Object item = it.next();
			count = count.add(thisArg.getCountOf(item));
		}
		return new BigFraction(count, thisArg.getTotal());
	}
}
