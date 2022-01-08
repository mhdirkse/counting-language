package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionIntersect extends AbstractMemberFunction {
	private DistributionsStrategy strategy;

	public DistributionIntersect() {
		super("intersect", CountlangType.distributionOfAny(), t -> t, t -> t);
		strategy = DistributionsStrategy.of(DistributionsStrategy.noUnknown("intersect"), DistributionsStrategy.otherNoUnknown("intersect"));
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		strategy.test(line, column, args);
		Distribution thisArg = (Distribution) args.get(0);
		Distribution other = (Distribution) args.get(1);
		Distribution.Builder b = new Distribution.Builder();
		for(Iterator<Object> it = thisArg.getItemIterator(); it.hasNext(); ) {
			Object item = it.next();
			BigInteger thisCount = thisArg.getCountOf(item);
			BigInteger otherCount = other.getCountOf(item);
			BigInteger newCount = thisCount.min(otherCount);
			if(newCount.compareTo(BigInteger.ZERO) > 0) {
				b.add(item, newCount);
			}
		}
		return b.build();
	}
}
