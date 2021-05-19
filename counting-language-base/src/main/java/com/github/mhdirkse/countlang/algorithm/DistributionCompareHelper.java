package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;

class DistributionCompareHelper {
    private final Distribution target;
    private final PossibilityValueIterator it;
    private ProbabilityTreeValue current;
    private BigInteger countToNext;

    DistributionCompareHelper(Distribution target) {
        if(target.getTotal().equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("DistributionCompareHelper does not support empty distributions");
        }
        this.target = target;
        this.it = new PossibilityValueIterator(target);
        initNext();
    }

    private void initNext() {
        current = it.next();
        countToNext = target.getCountOf(current);
    }

    ProbabilityTreeValue getCurrent() {
        return current;
    }

    BigInteger getCountToNext() {
        return countToNext;
    }

    boolean advance(BigInteger steps) {
        int compareStepsToCountToNext = steps.compareTo(countToNext);
        if(compareStepsToCountToNext > 0) {
            throw new IllegalArgumentException("Cannot advance further than start of next value");
        } else if(compareStepsToCountToNext == 0) {
            if(it.hasNext()) {
                initNext();
            } else {
                return true;
            }
        } else {
            countToNext = countToNext.subtract(steps);
        }
        return false;
    }
}
