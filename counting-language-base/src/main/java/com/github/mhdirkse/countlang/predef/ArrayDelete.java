package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayDelete extends AbstractMemberFunction {
	public ArrayDelete() {
		super("delete", CountlangType.arrayOfAny(), t -> CountlangType.arrayOf(t.getSubType()), t -> CountlangType.integer());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		BigInteger index = (BigInteger) args.get(1);
		checkArrayIndex(line, column, thisArg, index);
		int indexToDelete = index.intValue() - 1;
		List<Object> result = thisArg.getAll();
		result.remove(indexToDelete);
		return new CountlangArray(result);
	}
}
