package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.utils.Stack;

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

    abstract SampleContextImpl.SampledVariableInfo startSampledVariable(int line, int column, Stack<SampleContextImpl.SampledDistributionContext> sampleContexts, Distribution sampledDistribution);
    abstract void stopSampledVariable();
    abstract Distribution finishResult(Distribution raw);

    private static class SampledVariableInfoCalculation {
        private final int depth;
        private int index = 0;
        private int weight = 1;

        SampledVariableInfoCalculation(int depth) {
            this.depth = depth;
        }

        void nextContext(SampleContextImpl.SampledDistributionContext context) {
            if(index < (depth - 1)) {
                weight *= context.getCountOfCurrentValue();
            }
        }

        int getWeight() {
            return weight;
        }
    }

    static class CountingPossibilities extends PossibilityCountingValidityStrategy {
        private final List<CountNode> fixedPossibilityCountsPerDepth = new ArrayList<>();
        private int currentDepth = 0;

        @Override
        SampleContextImpl.SampledVariableInfo startSampledVariable(int line, int column, Stack<SampleContextImpl.SampledDistributionContext> sampleContexts, Distribution sampledDistribution) {
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
            currentDepth++;
            SampledVariableInfoCalculation calc = new SampledVariableInfoCalculation(currentDepth);
            sampleContexts.forEach(calc::nextContext);
            return new SampleContextImpl.SampledVariableInfo(refineFactor, calc.getWeight());
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
        SampleContextImpl.SampledVariableInfo startSampledVariable(int line, int column, Stack<SampleContextImpl.SampledDistributionContext> sampleContexts, Distribution sampledDistribution) {
            int weight = 1;
            int refineFactor = 1;
            if(!sampleContexts.isEmpty()) {
                int availableShare = sampleContexts.peek().getCountOfCurrent();
                int shareUpdate = leastCommonMultiplier(availableShare, sampledDistribution.getTotal());
                refineFactor = shareUpdate / availableShare;
                weight = shareUpdate / sampledDistribution.getTotal();
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
