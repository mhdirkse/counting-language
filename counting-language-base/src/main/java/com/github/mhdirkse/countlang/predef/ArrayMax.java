package com.github.mhdirkse.countlang.predef;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayMax extends AbstractMemberFunction {
	public ArrayMax() {
		super("max", CountlangType.arrayOfAny(), t -> t.getSubType());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		List<Object> sortedMembers = thisArg.getMembers().stream().sorted().collect(Collectors.toList());
		Collections.reverse(sortedMembers);
		return sortedMembers.get(0);
	}
}
