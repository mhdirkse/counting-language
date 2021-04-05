package com.github.mhdirkse.countlang.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.ProgramException;

abstract class PossibilityCountingValidityStrategy {
    abstract void startSampledVariable(Distribution sampledDistribution);
    abstract void stopSampledVariable();
    abstract Distribution finishResult(Distribution raw);

    static class CountingPossibilities extends PossibilityCountingValidityStrategy {
        private final List<Integer> fixedPossibilityCountsPerDepth = new ArrayList<>();
        private int currentDepth = 0;

        @Override
        void startSampledVariable(Distribution sampledDistribution) {
            if(currentDepth >= fixedPossibilityCountsPerDepth.size()) {
                fixedPossibilityCountsPerDepth.add(sampledDistribution.getTotal());
            } else {
                int fixedCount = fixedPossibilityCountsPerDepth.get(currentDepth);
                boolean fixedCountMatched = (fixedCount == sampledDistribution.getTotal());
                if(! fixedCountMatched) {
                    // TODO: Improve error message.
                    throw new ProgramException(0, 0, String.format("Only a distribution with %d possibilities is allowed, but got %d", fixedCount, sampledDistribution.getTotal()));
                }
            }
            currentDepth++;
        }

        @Override
        void stopSampledVariable() {
            currentDepth--;
        }

        @Override
        Distribution finishResult(Distribution raw) {
            return raw;
        }
    }

    static class NotCountingPossibilities extends PossibilityCountingValidityStrategy {
        @Override
        void startSampledVariable(Distribution sampledDistribution) {
        }

        @Override
        void stopSampledVariable() {
        }

        @Override
        Distribution finishResult(Distribution raw) {
            return raw.normalize();
        }
    }
}
