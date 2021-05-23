package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.ProgramException;

abstract class RefinementStrategy {
    static class CountNode {
        final int line;
        final int column;
        final BigInteger count;

        CountNode(final int line, final int column, final BigInteger count) {
            this.line = line;
            this.column = column;
            this.count = count;
        }
    }

    abstract BigInteger startSampledVariable(int line, int column, PossibilitiesWalker walker, Distribution sampledDistribution);
    abstract void stopSampledVariable();
    abstract Distribution finishResult(Distribution raw);

    static class CountingPossibilities extends RefinementStrategy {
        private final List<CountNode> fixedPossibilityCountsPerDepth = new ArrayList<>();
        private int currentDepth = 0;

        @Override
        BigInteger startSampledVariable(int line, int column, PossibilitiesWalker walker, Distribution sampledDistribution) {
            BigInteger result = checkFixedPossibilityCounts(line, column, sampledDistribution);
            currentDepth++;
            return result;
        }

        private BigInteger checkFixedPossibilityCounts(int line, int column, Distribution sampledDistribution) {
            BigInteger refineFactor = BigInteger.ONE;
            if(currentDepth >= fixedPossibilityCountsPerDepth.size()) {
                fixedPossibilityCountsPerDepth.add(new CountNode(line, column, sampledDistribution.getTotal()));
                refineFactor = sampledDistribution.getTotal();
            } else {
                CountNode fixedCountNode = fixedPossibilityCountsPerDepth.get(currentDepth);
                boolean fixedCountMatched = (fixedCountNode.count.equals(sampledDistribution.getTotal()));
                if(! fixedCountMatched) {
                    throw new ProgramException(line, column, String.format("Tried to sample from %s possibilities, but only %s possibilities are allowed, because from that amount was sampled at (%d, %d)",
                            sampledDistribution.getTotal().toString(), fixedCountNode.count.toString(), fixedCountNode.line, fixedCountNode.column));
                }
            }
            return refineFactor;
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

    static class NotCountingPossibilities extends RefinementStrategy {
        @Override
        BigInteger startSampledVariable(int line, int column, PossibilitiesWalker walker, Distribution sampledDistribution) {
            BigInteger availableShare = walker.getCount();
            BigInteger updatedShare = leastCommonMultiplier(availableShare, sampledDistribution.getTotal());
            return updatedShare.divide(availableShare);
        }

        private BigInteger leastCommonMultiplier(final BigInteger i1, final BigInteger i2) {
            BigInteger gcd = i1.gcd(i2);
            return i1.divide(gcd).multiply(i2);
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
