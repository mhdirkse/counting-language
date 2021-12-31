package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArraySize extends AbstractMemberFunction {
	public ArraySize() {
		super("size", CountlangType.arrayOfAny(), t -> CountlangType.integer());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		return ((CountlangArray) args.get(0)).size();
	}
}
