package com.github.mhdirkse.countlang.algorithm;

import java.util.Iterator;

class Edge {
    private Distribution distribution;
    private int weight;
    private Iterator<Object> iterator;
    private Object currentValue = null;

    Edge(Distribution distribution, int weight) {
        this.distribution = distribution;
        this.weight = weight;
        this.iterator = distribution.getItemIterator();
    }

    boolean hasValue() {
        return currentValue != null;
    }

    boolean hasNext() {
        return iterator.hasNext();
    }

    Object next() {
        currentValue = iterator.next();
        return currentValue;
    }

    int getCount() throws PossibilitiesWalkerException {
        if(currentValue == null) {
            throw new PossibilitiesWalkerException("Cannot give the count because no value has been selected");
        }
        return weight * distribution.getCountOf(currentValue);
    }

    int getCountUnknown() {
        return weight * distribution.getCountUnknown();
    }
}
