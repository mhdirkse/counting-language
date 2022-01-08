package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayMinRef extends AbstractMemberFunction {
	public ArrayMinRef() {
		super("minRef", CountlangType.arrayOfAny(), t -> CountlangType.integer());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		return thisArg.getSortRefs().get(0);
	}
}
