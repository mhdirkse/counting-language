package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;

class DistributionCompareHelper {
    private final Distribution target;
    private PossibilityValueIterator it;
    private ProbabilityTreeValue current;
    private BigInteger countToNext;
    private boolean isDone = false;

    DistributionCompareHelper(Distribution target) {
        this.target = target;
        if(target.getTotal().equals(BigInteger.ZERO)) {
            isDone = true;
        } else {
            this.it = new PossibilityValueIterator(target);
            initNext();
        }
    }

    private void initNext() {
        current = it.next();
        countToNext = target.getCountOf(current);
    }

    ProbabilityTreeValue getCurrent() {
        if(isDone) {
            throw new IllegalArgumentException("All values have been visited, no current value");
        }
        return current;
    }

    BigInteger getCountToNext() {
        if(isDone) {
            return BigInteger.ZERO;
        }
        return countToNext;
    }

    boolean isDone() {
        return getCountToNext().equals(BigInteger.ZERO);
    }

    void advance(BigInteger steps) {
        if(isDone) {
            throw new IllegalStateException("Cannot advance, all values have been visited");
        }
        int compareStepsToCountToNext = steps.compareTo(countToNext);
        if(compareStepsToCountToNext > 0) {
            throw new IllegalArgumentException("Cannot advance further than start of next value");
        } else if(compareStepsToCountToNext == 0) {
            if(it.hasNext()) {
                initNext();
            } else {
                isDone = true;
            }
        } else {
            countToNext = countToNext.subtract(steps);
        }
    }
}
