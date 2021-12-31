package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionAddAll extends AbstractMemberFunction {
	public DistributionAddAll() {
		super("addAll", CountlangType.distributionOfAny(), t -> t, t -> t);
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		Distribution orig = (Distribution) args.get(0);
		Distribution toAdd = (Distribution) args.get(1);
		Distribution.Builder b = new Distribution.Builder();
		addAll(b, orig);
		addAll(b, toAdd);
		return b.build();
	}

	private void addAll(Distribution.Builder result, Distribution toAdd) {
		toAdd.getItemIterator().forEachRemaining(v -> result.add(v, toAdd.getCountOf(v)));
		result.addUnknown(toAdd.getCountUnknown());
	}
}
