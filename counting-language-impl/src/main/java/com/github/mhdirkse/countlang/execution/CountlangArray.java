package com.github.mhdirkse.countlang.execution;

import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.utils.ListComparator;
import com.github.mhdirkse.countlang.utils.Utils;

class CountlangArray implements Comparable<CountlangArray> {
    private final List<Object> items;

    CountlangArray(List<Object> items) {
        this.items = items;
    }

    Object get(int i) {
        return items.get(i);
    }

    int size() {
        return items.size();
    }

    @Override
    public int compareTo(CountlangArray o) {
        return ListComparator.getInstance().compare(items, o.items);
    }

    @Override
    public String toString() {
        return "[" + items.stream().map(v -> Utils.genericFormat(v)).collect(Collectors.joining(", ")) + "]";
    }
}
