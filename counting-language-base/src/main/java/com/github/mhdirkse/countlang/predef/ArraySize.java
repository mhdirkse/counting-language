package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

// TODO: Add predefined functions min and max.
// TODO: Extend the RISK case to support more defending armies than attacker armies.
// TODO: Finish the RISK case.
public class ArraySize extends AbstractMemberFunction {
	public ArraySize() {
		super("size", CountlangType.arrayOfAny(), t -> CountlangType.integer());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		return BigInteger.valueOf(((CountlangArray) args.get(0)).size());
	}
}
