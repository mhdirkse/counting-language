package com.github.mhdirkse.countlang.predef;

import java.util.List;

public class ArrayAscending extends ArraySort {
    public ArrayAscending() {
        super("ascending");
    }

    @Override
    void afterSort(List<Comparable<Object>> values) {
        // Nothing to do, already sorted.
    }
}
