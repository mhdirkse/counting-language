/*
 * Copyright Martijn Dirkse 2021
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

    abstract BigInteger startSampledVariable(int line, int column, PossibilitiesWalker walker, Samplable sampledDistribution);
    abstract void stopSampledVariable();
    abstract Distribution finishResult(Distribution raw);

    static class CountingPossibilities extends RefinementStrategy {
        private final List<CountNode> fixedPossibilityCountsPerDepth = new ArrayList<>();
        private int currentDepth = 0;

        @Override
        BigInteger startSampledVariable(int line, int column, PossibilitiesWalker walker, Samplable sampledDistribution) {
            BigInteger result = checkFixedPossibilityCounts(line, column, sampledDistribution);
            currentDepth++;
            return result;
        }

        private BigInteger checkFixedPossibilityCounts(int line, int column, Samplable sampledDistribution) {
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
        BigInteger startSampledVariable(int line, int column, PossibilitiesWalker walker, Samplable sampledDistribution) {
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
