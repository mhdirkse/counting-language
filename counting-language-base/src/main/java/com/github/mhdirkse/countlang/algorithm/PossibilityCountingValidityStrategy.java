package com.github.mhdirkse.countlang.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.ProgramException;

abstract class PossibilityCountingValidityStrategy {
    static class CountNode {
        final int line;
        final int column;
        final int count;

        CountNode(final int line, final int column, final int count) {
            this.line = line;
            this.column = column;
            this.count = count;
        }
    }

    abstract void startSampledVariable(int line, int column, Distribution sampledDistribution);
    abstract void stopSampledVariable();
    abstract Distribution finishResult(Distribution raw);

    static class CountingPossibilities extends PossibilityCountingValidityStrategy {
        private final List<CountNode> fixedPossibilityCountsPerDepth = new ArrayList<>();
        private int currentDepth = 0;

        @Override
        void startSampledVariable(int line, int column, Distribution sampledDistribution) {
            if(currentDepth >= fixedPossibilityCountsPerDepth.size()) {
                fixedPossibilityCountsPerDepth.add(new CountNode(line, column, sampledDistribution.getTotal()));
            } else {
                CountNode fixedCountNode = fixedPossibilityCountsPerDepth.get(currentDepth);
                boolean fixedCountMatched = (fixedCountNode.count == sampledDistribution.getTotal());
                if(! fixedCountMatched) {
                    throw new ProgramException(line, column, String.format("Tried to sample from %d possibilities, but only %d possibilities are allowed, because from that amount was sampled at (%d, %d)",
                            sampledDistribution.getTotal(), fixedCountNode.count, fixedCountNode.line, fixedCountNode.column));
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
        void startSampledVariable(int line, int column, Distribution sampledDistribution) {
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
