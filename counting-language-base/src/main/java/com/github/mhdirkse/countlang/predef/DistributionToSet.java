package com.github.mhdirkse.countlang.predef;

import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionToSet extends AbstractMemberFunction {
	public DistributionToSet() {
		super("toSet", CountlangType.distributionOfAny(), t -> t);
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		Distribution thisArg = (Distribution) args.get(0);
		Distribution.Builder b = new Distribution.Builder();
		for(Iterator<Object> it = thisArg.getItemIterator(); it.hasNext();) {
			b.add(it.next());
		}
		return b.build();
	}
}
