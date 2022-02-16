package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayAscendingIndicesOf extends AbstractMemberFunction {
	public ArrayAscendingIndicesOf() {
		super("ascendingIndicesOf", CountlangType.arrayOfAny(), t -> CountlangType.arrayOf(CountlangType.integer()), t -> t.getSubType());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		Object toFind = args.get(1);
		List<Object> members = thisArg.getAll();
		List<BigInteger> result = new ArrayList<>();
		for(int i = 0; i < members.size(); ++i) {
			Object actual = members.get(i);
			if(actual.equals(toFind)) {
				long longIndex = i + 1;
				result.add(BigInteger.valueOf(longIndex));
			}
		}
		List<Object> objectList = new ArrayList<>();
		objectList.addAll(result);
		return new CountlangArray(objectList);
	}
}
