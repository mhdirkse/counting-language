package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionRemoveAll extends AbstractMemberFunction {
	private DistributionsStrategy strategy;
	
	public DistributionRemoveAll() {
		super("removeAll", CountlangType.distributionOfAny(), t -> t, t -> t);
		strategy = DistributionsStrategy.otherNoUnknown("removeAll");
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		strategy.test(line, column, args);
		Distribution.Builder b = new Distribution.Builder();
		Distribution thisArg = (Distribution) args.get(0);
		Distribution subtract = (Distribution) args.get(1);
		for(Iterator<Object> it = thisArg.getItemIterator(); it.hasNext(); ) {
			Object item = it.next();
			BigInteger currentCount = thisArg.getCountOf(item);
			BigInteger countToRemove = subtract.getCountOf(item);
			BigInteger newCount = currentCount.subtract(countToRemove);
			if(newCount.compareTo(BigInteger.ZERO) > 0) {
				b.add(item, newCount);
			}
		}
		if(thisArg.getCountUnknown().compareTo(BigInteger.ZERO) > 0) {
			b.addUnknown(thisArg.getCountUnknown());
		}
		return b.build();
	}
}
