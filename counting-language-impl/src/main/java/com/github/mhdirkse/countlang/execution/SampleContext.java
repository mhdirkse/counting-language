package com.github.mhdirkse.countlang.execution;

import java.math.BigInteger;
import java.util.Iterator;

import com.github.mhdirkse.countlang.types.Distribution;
import com.github.mhdirkse.countlang.utils.Stack;

class SampleContext {
    private final Distribution.Builder distributionBuilder = new Distribution.Builder();

    private static class SampledDistributionContext {
        final private Distribution sampledDistribution;
        final private Iterator<Integer> sampledValues;
        private int weight;
        private boolean hasCurrentValue = false;
        private int currentValue;

        SampledDistributionContext(final Distribution sampledDistribution, final int initialWeight) {
            this.sampledDistribution = sampledDistribution;
            this.sampledValues = sampledDistribution.getItemIterator();
            this.weight = initialWeight;
        }

        boolean hasNextValue() {
            return sampledValues.hasNext();
        }

        Integer nextValue() {
            hasCurrentValue = true;
            currentValue = sampledValues.next();
            return currentValue;
        }

        int getCountOfCurrent() {
            if(!hasCurrentValue) {
                throw new IllegalStateException("Cannot score a result before a value was sampled");
            }
            return weight * sampledDistribution.getCountOf(currentValue);
        }

        int getCountUnknown() {
            return weight * sampledDistribution.getCountUnknown();
        }

        void refine(int factor) {
            weight *= factor;
        }
    }

    private final Stack<SampledDistributionContext> sampleContexts = new Stack<>();
    private boolean isScored = false;

    void startSampledVariable(final Distribution sampledDistribution) {
        checkScoreOnce();
        int weight = 1;
        int refineFactor = 1;
        if(!sampleContexts.isEmpty()) {
            int availableShare = sampleContexts.peek().weight;
            int shareUpdate = leastCommonMultiplier(availableShare, sampledDistribution.getTotal());
            refineFactor = shareUpdate / availableShare;
            weight = shareUpdate / sampledDistribution.getTotal();
        }
        if(refineFactor > 1) {
            final int fixed = refineFactor;
            sampleContexts.forEach(sc -> sc.refine(fixed));
            distributionBuilder.refine(refineFactor);
        }
        sampleContexts.push(new SampledDistributionContext(sampledDistribution, weight));
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

    void stopSampledVariable() {
        checkScoringNotForgotten();
        if(sampleContexts.isEmpty()) {
            throw new IllegalStateException("No sampled variables left");
        }
        if(sampleContexts.peek().hasNextValue()) {
            throw new IllegalStateException("Not all values have been sampled for this variable");
        }
        SampledDistributionContext stoppedSampleContext = sampleContexts.pop();
        distributionBuilder.addUnknown(stoppedSampleContext.getCountUnknown());
        isScored = true;
    }

    boolean hasNextValue() {
        return sampleContexts.peek().hasNextValue();
    }

    int nextValue() {
        checkScoringNotForgotten();
        isScored = false;
        return sampleContexts.peek().nextValue();
    }

    private void checkScoringNotForgotten() {
        if(!isScored) {
            throw new IllegalStateException("Please score a result value before sampleing the next value");
        }
    }

    void score(int value) {
        checkScoreOnce();
        distributionBuilder.add(value, sampleContexts.peek().getCountOfCurrent());
    }

    private void checkScoreOnce() {
        if(isScored) {
            throw new IllegalStateException("Cannot score twice for the same sample");
        }
        isScored = true;
    }

    void scoreUnknown() {
        checkScoreOnce();
        distributionBuilder.addUnknown(sampleContexts.peek().getCountOfCurrent());
    }

    Distribution getResult() {
        if(!sampleContexts.isEmpty()) {
            throw new IllegalStateException("Cannot get result distribution because sampling is not finished");
        }
        return distributionBuilder.build();
    }
}
