package com.github.mhdirkse.countlang.predef;

import java.util.List;

public class DistributionAscending extends AbstractDistributionToArray {
    public DistributionAscending() {
        super("ascending");
    }

    @Override
    void afterSort(List<Comparable<Object>> result) {
        // Nothing to do, already sorted ascending.
    }
}
