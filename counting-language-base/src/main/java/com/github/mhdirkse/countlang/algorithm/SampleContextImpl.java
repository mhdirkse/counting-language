/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.algorithm;

import java.util.Iterator;

import com.github.mhdirkse.countlang.utils.Stack;

class SampleContextImpl implements SampleContext {
    static class SampledDistributionContext {
        final private Distribution sampledDistribution;
        final private Iterator<Object> sampledValues;
        private int weight;
        private boolean hasCurrentValue = false;
        private Object currentValue;

        SampledDistributionContext(final Distribution sampledDistribution, final int initialWeight) {
            this.sampledDistribution = sampledDistribution;
            this.sampledValues = sampledDistribution.getItemIterator();
            this.weight = initialWeight;
        }

        boolean hasNextValue() {
            return sampledValues.hasNext();
        }

        Object nextValue() {
            hasCurrentValue = true;
            currentValue = sampledValues.next();
            return currentValue;
        }

        int getCountOfCurrent() {
            if(!hasCurrentValue) {
                throw new IllegalStateException("Cannot score a result before a value was sampled");
            }
            return weight * getCountOfCurrentValue();
        }

        int getCountOfCurrentValue() {
            return sampledDistribution.getCountOf(currentValue);
        }

        int getCountUnknown() {
            return weight * sampledDistribution.getCountUnknown();
        }

        void refine(int factor) {
            weight *= factor;
        }
    }

    static class SampledVariableInfo {
        int refineFactor;
        int weight;

        SampledVariableInfo(int refineFactor, int weight) {
            this.refineFactor = refineFactor;
            this.weight = weight;
        }
    }

    private final Distribution.Builder distributionBuilder = new Distribution.Builder();
    private final PossibilityCountingValidityStrategy possibilityCountingValidityStrategy;
    private final Stack<SampledDistributionContext> sampleContexts = new Stack<>();
    private boolean isScored = false;
    private boolean isFinished = false;

    SampleContextImpl(PossibilityCountingValidityStrategy possibilityCountingValidityStrategy) {
        this.possibilityCountingValidityStrategy = possibilityCountingValidityStrategy;
    }

    @Override
    public void startSampledVariable(int line, int column, final Distribution sampledDistribution) {
        checkScoreOnce();
        SampledVariableInfo info = possibilityCountingValidityStrategy.startSampledVariable(line, column, sampleContexts, sampledDistribution);
        if(info.refineFactor > 1) {
            final int fixed = info.refineFactor;
            sampleContexts.forEach(sc -> sc.refine(fixed));
            distributionBuilder.refine(info.refineFactor);
        }
        sampleContexts.push(new SampledDistributionContext(sampledDistribution, info.weight));
    }

    @Override
    public void stopSampledVariable() {
        checkScoringNotForgotten();
        possibilityCountingValidityStrategy.stopSampledVariable();
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

    @Override
    public boolean hasNextValue() {
        return sampleContexts.peek().hasNextValue();
    }

    @Override
    public Object nextValue() {
        checkScoringNotForgotten();
        isScored = false;
        return sampleContexts.peek().nextValue();
    }

    private void checkScoringNotForgotten() {
        if(!isScored) {
            throw new IllegalStateException("Please score a result value before sampleing the next value");
        }
    }

    @Override
    public void score(Object value) {
        checkScoreOnce();
        distributionBuilder.add(value, getScoreCount());
    }

    private int getScoreCount() {
        if(sampleContexts.size() == 0) {
            return 1;
        }
        return sampleContexts.peek().getCountOfCurrent();
    }

    private void checkScoreOnce() {
        if(isScored) {
            throw new IllegalStateException("Cannot score twice for the same sample");
        }
        isScored = true;
    }

    @Override
    public void scoreUnknown() {
        checkScoreOnce();
        distributionBuilder.addUnknown(getScoreCount());
    }

    @Override
    public Distribution getResult() {
        if(!sampleContexts.isEmpty()) {
            throw new IllegalStateException("Cannot get result distribution because sampling is not finished");
        }
        Distribution result = distributionBuilder.build();
        if(! isFinished) {
            isFinished = true;
            return possibilityCountingValidityStrategy.finishResult(result);
        } else {
            return result;
        }
    }

    @Override
    public boolean isScored() {
        return isScored;
    }
}
