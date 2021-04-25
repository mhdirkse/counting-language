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

import com.github.mhdirkse.countlang.ast.ProgramException;

class SampleContextImpl implements SampleContext {
    PossibilitiesWalker walker = new PossibilitiesWalker();

    private final Distribution.Builder distributionBuilder = new Distribution.Builder();
    private final RefinementStrategy refinementStrategy;
    private boolean isScored = false;

    SampleContextImpl(RefinementStrategy refinementStrategy) {
        this.refinementStrategy = refinementStrategy;
    }

    @Override
    public void startSampledVariable(int line, int column, final Distribution sampledDistribution) throws ProgramException {
        checkScoreOnce();
        int refineFactor = refinementStrategy.startSampledVariable(line, column, walker, sampledDistribution);
        if(refineFactor > 1) {
            try {
                walker.refine(refineFactor);
            } catch(PossibilitiesWalkerException e) {
                throw new ProgramException(line, column, refinementStrategy.getOverflowMessage());
            }
            distributionBuilder.refine(refineFactor);
        }
        walker.down(sampledDistribution);
    }

    @Override
    public void stopSampledVariable() {
        checkScoringNotForgotten();
        refinementStrategy.stopSampledVariable();
        if(walker.getNumEdges() == 0) {
            throw new IllegalStateException("No sampled variables left");
        }
        if(walker.hasNext()) {
            throw new IllegalStateException("Not all values have been sampled for this variable");
        }
        walker.up();
        isScored = true;
    }

    @Override
    public boolean hasNextValue() {
        return walker.hasNext();
    }

    @Override
    public ProbabilityTreeValue nextValue() {
        checkScoringNotForgotten();
        isScored = false;
        return walker.next();
    }

    private void checkScoringNotForgotten() {
        if(!isScored) {
            throw new IllegalStateException("Please score a result value before sampleing the next value");
        }
    }

    @Override
    public void score(Object value) {
        checkScoreOnce();
        distributionBuilder.add(value, walker.getCount());
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
        distributionBuilder.addUnknown(walker.getCount());
    }

    @Override
    public Distribution getResult() {
        if(walker.getNumEdges() != 0) {
            throw new IllegalStateException("Cannot get result distribution because sampling is not finished");
        }
        Distribution result = distributionBuilder.build();
        return refinementStrategy.finishResult(result);
    }

    @Override
    public boolean isScored() {
        return isScored;
    }
}
