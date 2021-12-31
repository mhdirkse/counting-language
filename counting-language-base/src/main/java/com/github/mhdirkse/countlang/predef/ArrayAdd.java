package com.github.mhdirkse.countlang.predef;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayAdd extends AbstractMemberFunction {
	@SuppressWarnings("unchecked")
	public ArrayAdd() {
		super("add", CountlangType.arrayOfAny(), t -> CountlangType.arrayOf(t.getSubType()), t -> t.getSubType());
	}

	@SuppressWarnings("unchecked")
	@Override
    public Object run(int line, int column, List<Object> args) {
        // Copying is done by CountlangArray.
        List<Comparable<Object>> values = ((CountlangArray) args.get(0)).getMembers();
        values.add((Comparable<Object>) args.get(1));
        return new CountlangArray(new ArrayList<>(values));
    }
}
