package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayMaxRef extends AbstractMemberFunction {
	public ArrayMaxRef() {
		super("maxRef", CountlangType.arrayOfAny(), t -> CountlangType.integer());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		List<BigInteger> sortRefs = thisArg.getSortRefs();
		Collections.reverse(sortRefs);
		return sortRefs.get(0);
	}
}
