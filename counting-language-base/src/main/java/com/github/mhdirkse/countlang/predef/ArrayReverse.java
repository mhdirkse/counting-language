package com.github.mhdirkse.countlang.predef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayReverse extends AbstractMemberFunction {
	public ArrayReverse() {
		super("reverse", CountlangType.arrayOfAny(), t -> t);
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray arg = (CountlangArray) args.get(0);
		List<Object> newMembers = new ArrayList<Object>(arg.getMembers());
		Collections.reverse(newMembers);
		return new CountlangArray(newMembers);
	}

}
