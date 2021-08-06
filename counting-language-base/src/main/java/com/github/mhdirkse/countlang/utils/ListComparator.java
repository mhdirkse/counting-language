package com.github.mhdirkse.countlang.utils;

import java.util.Comparator;
import java.util.List;

public final class ListComparator implements Comparator<List<Object>> {
    private static ListComparator instance = null;

    public static ListComparator getInstance() {
        if(instance == null) {
            instance = new ListComparator();
        }
        return instance;
    }

    private ListComparator() {
    }

    @Override
    public int compare(List<Object> arg0, List<Object> arg1) {
        int numComparable = Math.min(arg0.size(), arg1.size());
        for(int i = 0; i < numComparable; ++i) {
            @SuppressWarnings("unchecked")
            Comparable<Object> first = (Comparable<Object>) arg0.get(i);
            @SuppressWarnings("unchecked")
            Comparable<Object> second = (Comparable<Object>) arg1.get(i);
            int result = first.compareTo(second);
            if(result != 0) {
                return result;
            }
        }
        return Integer.valueOf(arg0.size()).compareTo(arg1.size());
    }
}
