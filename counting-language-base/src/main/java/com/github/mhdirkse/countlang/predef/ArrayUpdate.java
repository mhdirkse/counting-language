package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayUpdate extends AbstractMemberFunction {
	public ArrayUpdate() {
		super("update", CountlangType.arrayOfAny(), t -> CountlangType.arrayOf(t.getSubType()), t -> CountlangType.integer(), t -> t.getSubType());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		BigInteger index = (BigInteger) args.get(1);
		Object newValue = args.get(2);
		checkArrayIndex(line, column, thisArg, index);
		List<Object> result = thisArg.getAll();
		int indexToChange = index.intValue() - 1;
		result.set(indexToChange, newValue);
		return new CountlangArray(result);
	}
}
