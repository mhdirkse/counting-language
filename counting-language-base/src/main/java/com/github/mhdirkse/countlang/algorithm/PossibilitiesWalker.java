package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;

import com.github.mhdirkse.countlang.utils.Stack;

/**
 * Walks a probability tree as explained with {@link SampleContext}.
 * @author martijn
 *
 */
class PossibilitiesWalker {
    private BigInteger total = BigInteger.ONE;
    private Stack<Edge> edges = new Stack<>();

    PossibilitiesWalker() {
    }

    int getNumEdges() {
        return edges.size();
    }

    void down(Distribution distribution) {
        if(distribution.getTotal().compareTo(BigInteger.ZERO) == 0) {
            throw new IllegalArgumentException("Cannot iterate over an empty distribution");
        }
        if(getCount().mod(distribution.getTotal()).compareTo(BigInteger.ZERO) != 0) {
            throw new IllegalArgumentException(String.format("Cannot fit distribution with total %d in count %d", distribution.getTotal(), total));
        }
        edges.push(new Edge(distribution, getCount().divide(distribution.getTotal())));
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

    BigInteger getCount() {
        if(edges.isEmpty()) {
            return total;
        } else {
            return edges.peek().getCount();
        }
    }

    BigInteger getTotal() {
        return total;
    }

    void refine(int factor) {
        refine(new BigInteger(Integer.valueOf(factor).toString()));
    }

    void refine(BigInteger factor) {
        total = total.multiply(factor);
        edges.forEach(e -> e.refine(factor));        
    }
}
