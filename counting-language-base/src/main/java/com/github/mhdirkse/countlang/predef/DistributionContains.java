package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionContains extends AbstractMemberFunction {
	private DistributionsStrategy strategy;

	public DistributionContains() {
		super("contains", CountlangType.distributionOfAny(), t -> CountlangType.bool(), t -> t);
		strategy = DistributionsStrategy.otherNoUnknown("contains");
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		strategy.test(line, column, args);
		Distribution thisArg = (Distribution) args.get(0);
		Distribution other = (Distribution) args.get(1);
		for(Iterator<Object> it = other.getItemIterator(); it.hasNext(); ) {
			Object testItem = it.next();
			BigInteger requiredCount = other.getCountOf(testItem);
			BigInteger availableCount = thisArg.getCountOf(testItem);
			if(availableCount.compareTo(requiredCount) < 0) {
				return false;
			}
		}
		return true;
	}
}
