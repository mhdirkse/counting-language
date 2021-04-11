package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.utils.Stack;

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

    abstract SampleContextImpl.SampledVariableInfo startSampledVariable(int line, int column, Stack<SampleContextImpl.SampledDistributionContext> sampleContexts, Distribution sampledDistribution);
    abstract void stopSampledVariable();
    abstract Distribution finishResult(Distribution raw);

    static class CountingPossibilities extends RefinementStrategy {
        private final List<CountNode> fixedPossibilityCountsPerDepth = new ArrayList<>();
        private int currentDepth = 0;

        @Override
        SampleContextImpl.SampledVariableInfo startSampledVariable(int line, int column, Stack<SampleContextImpl.SampledDistributionContext> sampleContexts, Distribution sampledDistribution) {
            int refineFactor = checkFixedPossibilityCounts(line, column, sampledDistribution);
            // Do not confuse the sample contexts with the fixed weight context.
            // A sample context is popped when sampling a variable stops. We can
            // work here with all sample context we have; limiting by the current
            // depth is confusing two different things.
            //
            // We cannot use the last SampledDistributionContext's getCumulativeCount()
            // method, because doing so would confuse the weight before refinement and the
            // weight after refinement. We recalculate here the amount of possibilities
            // for arriving at the parent node, without considering the sub-experiment of
            // the child.
            int weight = sampleContexts.applyToAll(SampleContextImpl.SampledDistributionContext::getCountOfCurrentValue)
                    .reduce(1, (a, b) -> a * b);
            currentDepth++;
            return new SampleContextImpl.SampledVariableInfo(refineFactor, weight);
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
    }

    static class NotCountingPossibilities extends RefinementStrategy {
        @Override
        SampleContextImpl.SampledVariableInfo startSampledVariable(int line, int column, Stack<SampleContextImpl.SampledDistributionContext> sampleContexts, Distribution sampledDistribution) {
            int weight = 1;
            int refineFactor = 1;
            if(!sampleContexts.isEmpty()) {
                int availableShare = sampleContexts.peek().getCumulativeCount();
                int updatedShare = 1;
                try {
                    updatedShare = leastCommonMultiplier(availableShare, sampledDistribution.getTotal());
                } catch(ArithmeticException e) {
                    throw new ProgramException(line, column, "Integer overflow when calculating a common denominator for the result distribution. You can fix this by using distributions with smaller totals or by using distributions whose totals have a large common factor (e.g. 10000 and 100)");
                }
                refineFactor = updatedShare / availableShare;
                weight = updatedShare / sampledDistribution.getTotal();
            }
            return new SampleContextImpl.SampledVariableInfo(refineFactor, weight);
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
    }
}
