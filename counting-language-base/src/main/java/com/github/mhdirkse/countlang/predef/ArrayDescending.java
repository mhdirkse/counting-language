package com.github.mhdirkse.countlang.predef;

import java.util.Collections;
import java.util.List;

public class ArrayDescending extends ArraySort {
    public ArrayDescending() {
        super("descending");
    }

    @Override
    void afterSort(List<Comparable<Object>> values) {
        Collections.reverse(values);
    }
}
