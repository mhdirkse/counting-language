package com.github.mhdirkse.countlang.algorithm;

class Edge {
    private Distribution distribution;
    private int weight;
    private PossibilityValueIterator iterator;
    private ProbabilityTreeValue currentValue = null;

    Edge(Distribution distribution, int weight) {
        this.distribution = distribution;
        this.weight = weight;
        this.iterator = new PossibilityValueIterator(distribution);
    }

    boolean hasValue() {
        return currentValue != null;
    }

    boolean hasNext() {
        return iterator.hasNext();
    }

    ProbabilityTreeValue next() {
        currentValue = iterator.next();
        return currentValue;
    }

    int getCount() throws PossibilitiesWalkerException {
        if(currentValue == null) {
            throw new PossibilitiesWalkerException("Cannot give the count because no value has been selected");
        }
        return weight * distribution.getCountOf(currentValue);
    }

    void refine(int factor) {
        weight *= factor;
    }
}
