package com.github.mhdirkse.countlang.algorithm;

import java.util.Iterator;

class PossibilityValueIterator implements Iterator<ProbabilityTreeValue> {
    private final Iterator<Object> normalValueIterator;
    private final boolean distributionIncludesUnknown;
    private boolean returnedUnknown = false;

    PossibilityValueIterator(Distribution subject) {
        normalValueIterator = subject.getItemIterator();
        distributionIncludesUnknown = subject.getCountUnknown() >= 1;
    }

    @Override
    public boolean hasNext() {
        boolean returnStillToReturn = distributionIncludesUnknown && (! returnedUnknown);
        return normalValueIterator.hasNext() || returnStillToReturn;
    }

    @Override
    public ProbabilityTreeValue next() {
        if(! hasNext()) {
            throw new IllegalArgumentException("PossibilityValueIterator has no more values");
        }
        if(normalValueIterator.hasNext()) {
            return ProbabilityTreeValue.of(normalValueIterator.next());
        }
        returnedUnknown = true;
        return ProbabilityTreeValue.unknown();
    }
}
