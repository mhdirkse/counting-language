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

    int getNumEdges() {
        return edges.size();
    }

    void down(Distribution distribution) {
        if(distribution.getTotal() == 0) {
            throw new IllegalArgumentException("Cannot iterate over an empty distribution");
        }
        if((getCount() % distribution.getTotal()) != 0) {
            throw new IllegalArgumentException(String.format("Cannot fit distribution with total %d in count %d", distribution.getTotal(), total));
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

    int getCount() {
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
        long newTotal = ((long) total) * factor;
        if(newTotal > Integer.MAX_VALUE) {
            throw new PossibilitiesWalkerException(String.format("Integer overflow when refining total %d with factor %d", total, factor));
        }
        total = (int) newTotal;
        edges.forEach(e -> e.refine(factor));
    }
}
