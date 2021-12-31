package com.github.mhdirkse.countlang.predef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

abstract class ArraySort extends AbstractMemberFunction {
    @SuppressWarnings("unchecked")
	ArraySort(String name) {
        super(name, CountlangType.arrayOfAny(), t -> t);
    }

    @Override
    public Object run(int line, int column, List<Object> args) {
        // Copying is done by CountlangArray.
        List<Comparable<Object>> values = ((CountlangArray) args.get(0)).getMembers();
        Collections.sort(values);
        afterSort(values);
        return new CountlangArray(new ArrayList<>(values));
    }

    abstract void afterSort(List<Comparable<Object>> values);
}
