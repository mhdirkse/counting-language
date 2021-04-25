package com.github.mhdirkse.countlang.algorithm;

import com.github.mhdirkse.countlang.utils.Stack;

/**
 * Walks a probability tree as explained with {@link SampleContext}.
 * @author martijn
 *
 */
class PossibilitiesWalker {
    private int total = 1;
    private Stack<Edge> edges = new Stack<>();

    PossibilitiesWalker() {
    }

    void down(Distribution distribution) throws PossibilitiesWalkerException {
        if(distribution.getTotal() == 0) {
            throw new PossibilitiesWalkerException("Cannot iterate over an empty distribution");
        }
        if((getCount() % distribution.getTotal()) != 0) {
            throw new PossibilitiesWalkerException.NewDistributionDoesNotFitParentCount(distribution.getTotal(), total);
        }
        edges.push(new Edge(distribution, getCount() / distribution.getTotal()));
    }

    void up() {
        edges.pop();
    }

    boolean hasNext() {
        return (edges.size() >= 1) && edges.peek().hasNext();
    }

    ProbabilityTreeValue next() {
        return edges.peek().next();
    }

    int getCount() throws PossibilitiesWalkerException {
        if(edges.isEmpty()) {
            return total;
        } else {
            return edges.peek().getCount();
        }
    }

    int getTotal() {
        return total;
    }

    void refine(int factor) throws PossibilitiesWalkerException {
        total *= factor;
        edges.forEach(e -> e.refine(factor));
    }
}
