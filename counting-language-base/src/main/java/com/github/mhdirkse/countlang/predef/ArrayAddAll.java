package com.github.mhdirkse.countlang.predef;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayAddAll extends AbstractMemberFunction {
	@SuppressWarnings("unchecked")
	public ArrayAddAll() {
		super("addAll", CountlangType.arrayOfAny(), t -> t, t -> t);
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		CountlangArray addArg = (CountlangArray) args.get(1);
		List<Object> elements = new ArrayList<>(thisArg.getAll());
		elements.addAll(addArg.getAll());
		return new CountlangArray(elements);
	}
}
