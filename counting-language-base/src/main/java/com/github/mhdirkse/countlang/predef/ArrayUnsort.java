package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayUnsort extends AbstractMemberFunction {
	@SuppressWarnings("unchecked")
	public ArrayUnsort() {
		super("unsort", CountlangType.arrayOfAny(), t -> CountlangType.distributionOf(t.getSubType()));
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		Distribution.Builder b = new Distribution.Builder();
		thisArg.getMembers().forEach(m -> b.add(m));
		return b.build();
	}
}
