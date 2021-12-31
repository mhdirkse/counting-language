package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionIsSet extends AbstractMemberFunction {
	public DistributionIsSet() {
		super("isSet", CountlangType.distributionOfAny(), t -> CountlangType.bool());
	}

	@Override
	public Boolean run(int line, int column, List<Object> args) {
		Distribution thisArg = (Distribution) args.get(0);
		if(thisArg.getCountUnknown().compareTo(BigInteger.ZERO) != 0) {
			return false;
		}
		for(Iterator<Object> it = thisArg.getItemIterator(); it.hasNext();) {
			Object value = it.next();
			if(thisArg.getCountOf(value).compareTo(BigInteger.ONE) != 0) {
				return false;
			}
		}
		return true;
	}
}
