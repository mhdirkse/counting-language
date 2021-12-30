package com.github.mhdirkse.countlang.predef;

import java.util.Collections;
import java.util.List;

public class DistributionDescending extends AbstractDistributionToArray {
    public DistributionDescending() {
        super("descending");
    }

    @Override
    void afterSort(List<Comparable<Object>> result) {
        Collections.reverse(result);
    }
}
