package com.github.mhdirkse.countlang.algorithm;

/**
 * Walks a probability tree as explained with {@link SampleContext}.
 * @author martijn
 *
 */
class PossibilitiesWalker {
    private int total = 1;

    PossibilitiesWalker() {
    }

    boolean isAtRoot() {
        return true;
    }

    boolean isAtNewLeaf() {
        return true;
    }

    void down(Distribution distribution) throws PossibilitiesWalkerException {
        if(distribution.getTotal() == 0) {
            throw new PossibilitiesWalkerException("Cannot iterate over an empty distribution");
        }
        if((total % distribution.getTotal()) != 0) {
            throw new PossibilitiesWalkerException.NewDistributionDoesNotFitParentCount(distribution.getTotal(), total);
        }
    }

    void up() {
        // TODO: Implement.
    }

    boolean hasNext() {
        return false;
    }

    ProbabilityTreeValue next() {
        // TODO: Implement
        return null;
    }

    int getCount() {
        return 1;
    }

    int getTotal() {
        return 1;
    }

    int getNumEdges() {
        return 0;
    }

    void refine(int factor) throws PossibilitiesWalkerException {
        // TODO: Implement
    }
}
