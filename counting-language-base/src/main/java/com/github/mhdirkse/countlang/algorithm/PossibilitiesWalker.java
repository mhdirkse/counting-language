package com.github.mhdirkse.countlang.algorithm;

/**
 * Walks a probability tree as explained with {@link SampleContext}.
 * @author martijn
 *
 */
class PossibilitiesWalker {
    boolean atStart = true;
    private int total = 1;
    private Edge edge = null;

    PossibilitiesWalker() {
    }

    void down(Distribution distribution) throws PossibilitiesWalkerException {
        if(distribution.getTotal() == 0) {
            throw new PossibilitiesWalkerException("Cannot iterate over an empty distribution");
        }
        if((total % distribution.getTotal()) != 0) {
            throw new PossibilitiesWalkerException.NewDistributionDoesNotFitParentCount(distribution.getTotal(), total);
        }
        atStart = false;
        edge = new Edge(distribution, total / distribution.getTotal());
    }

    void up() {
        edge = null;
    }

    boolean hasNext() {
        return (edge != null) && edge.hasNext();
    }

    Object next() {
        return edge.next();
    }

    int getCount() throws PossibilitiesWalkerException {
        if(atStart) {
            return 1;
        } else {
            return edge.getCount();
        }
    }

    int getCountUnknown() {
        return edge.getCountUnknown();
    }

    int getTotal() {
        return total;
    }

    int getNumEdges() throws PossibilitiesWalkerException {
        if(edge == null) {
            return 0;
        } else if(edge.hasValue()) {
            return 1;
        } else {
            return 0;
        }
    }

    void refine(int factor) throws PossibilitiesWalkerException {
        total *= factor;
    }
}
