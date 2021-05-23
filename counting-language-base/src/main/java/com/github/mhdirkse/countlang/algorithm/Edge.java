package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;

class Edge {
    private Distribution distribution;
    private BigInteger weight;
    private PossibilityValueIterator iterator;
    private ProbabilityTreeValue currentValue = null;

    Edge(Distribution distribution, BigInteger weight) {
        this.distribution = distribution;
        this.weight = weight;
        this.iterator = new PossibilityValueIterator(distribution);        
    }

    boolean hasNext() {
        return iterator.hasNext();
    }

    ProbabilityTreeValue next() {
        currentValue = iterator.next();
        return currentValue;
    }

    BigInteger getCount() {
        if(currentValue == null) {
            throw new IllegalStateException("Cannot give the count because no value has been selected");
        }
        return weight.multiply(distribution.getCountOf(currentValue));
    }

    void refine(BigInteger factor) {
        weight = weight.multiply(factor);
    }
}
