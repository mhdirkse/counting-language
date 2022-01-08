package com.github.mhdirkse.countlang.predef;

import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayMin extends AbstractMemberFunction {
	public ArrayMin() {
		super("min", CountlangType.arrayOfAny(), t -> t.getSubType());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		List<Object> sortedMembers = thisArg.getMembers().stream().sorted().collect(Collectors.toList());
		return sortedMembers.get(0);
	}
}
