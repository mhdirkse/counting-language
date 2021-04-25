package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.ProgramException;

abstract class RefinementStrategy {
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

    abstract int startSampledVariable(int line, int column, PossibilitiesWalker walker, Distribution sampledDistribution);
    abstract void stopSampledVariable();
    abstract Distribution finishResult(Distribution raw);
    abstract String getOverflowMessage();

    static class CountingPossibilities extends RefinementStrategy {
        private final List<CountNode> fixedPossibilityCountsPerDepth = new ArrayList<>();
        private int currentDepth = 0;

        @Override
        int startSampledVariable(int line, int column, PossibilitiesWalker walker, Distribution sampledDistribution) {
            int result = checkFixedPossibilityCounts(line, column, sampledDistribution);
            currentDepth++;
            return result;
        }

        private int checkFixedPossibilityCounts(int line, int column, Distribution sampledDistribution) {
            int refineFactor = 1;
            if(currentDepth >= fixedPossibilityCountsPerDepth.size()) {
                fixedPossibilityCountsPerDepth.add(new CountNode(line, column, sampledDistribution.getTotal()));
                refineFactor = sampledDistribution.getTotal();
            } else {
                CountNode fixedCountNode = fixedPossibilityCountsPerDepth.get(currentDepth);
                boolean fixedCountMatched = (fixedCountNode.count == sampledDistribution.getTotal());
                if(! fixedCountMatched) {
                    throw new ProgramException(line, column, String.format("Tried to sample from %d possibilities, but only %d possibilities are allowed, because from that amount was sampled at (%d, %d)",
                            sampledDistribution.getTotal(), fixedCountNode.count, fixedCountNode.line, fixedCountNode.column));
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

        @Override
        String getOverflowMessage() {
            return "Integer overflow when calculating the total number of possibilities";
        }
    }

    static class NotCountingPossibilities extends RefinementStrategy {
        @Override
        int startSampledVariable(int line, int column, PossibilitiesWalker walker, Distribution sampledDistribution) {
            int availableShare = walker.getCount();
            int updatedShare = 1;
            try {
                updatedShare = leastCommonMultiplier(availableShare, sampledDistribution.getTotal());
            } catch(ArithmeticException e) {
                throw new ProgramException(line, column, "Integer overflow when calculating a common denominator for the result distribution. You can fix this by using distributions with smaller totals or by using distributions whose totals have a large common factor (e.g. 10000 and 100)");
            }
            return updatedShare / availableShare;
        }

        private int leastCommonMultiplier(final int i1, final int i2) {
            BigInteger bi1 = BigInteger.valueOf(i1);
            BigInteger bi2 = BigInteger.valueOf(i2);
            BigInteger gcd = bi1.gcd(bi2);
            BigInteger result = bi1.divide(gcd).multiply(bi2);
            if(result.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                throw new ArithmeticException(
                        String.format("Least common multiplier overflow, for %d and %d",
                                i1, i2));
            }
            return result.intValue();
        }

        @Override
        void stopSampledVariable() {
        }

        @Override
        Distribution finishResult(Distribution raw) {
            return raw.normalize();
        }

        @Override
        String getOverflowMessage() {
            return "Integer overflow when calculating a common denominator";
        }
    }
}
