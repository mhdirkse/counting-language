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

class DistributionCompareHelper {
    private final Distribution target;
    private PossibilityValueIterator it;
    private ProbabilityTreeValue current;
    private BigInteger countToNext;
    private boolean isDone = false;

    DistributionCompareHelper(Distribution target) {
        this.target = target;
        if(target.getTotal().equals(BigInteger.ZERO)) {
            isDone = true;
        } else {
            this.it = new PossibilityValueIterator(target);
            initNext();
        }
    }

    private void initNext() {
        current = it.next();
        countToNext = target.getCountOf(current);
    }

    ProbabilityTreeValue getCurrent() {
        if(isDone) {
            throw new IllegalArgumentException("All values have been visited, no current value");
        }
        return current;
    }

    BigInteger getCountToNext() {
        if(isDone) {
            return BigInteger.ZERO;
        }
        return countToNext;
    }

    boolean isDone() {
        return getCountToNext().equals(BigInteger.ZERO);
    }

    void advance(BigInteger steps) {
        if(isDone) {
            throw new IllegalStateException("Cannot advance, all values have been visited");
        }
        int compareStepsToCountToNext = steps.compareTo(countToNext);
        if(compareStepsToCountToNext > 0) {
            throw new IllegalArgumentException("Cannot advance further than start of next value");
        } else if(compareStepsToCountToNext == 0) {
            if(it.hasNext()) {
                initNext();
            } else {
                isDone = true;
            }
        } else {
            countToNext = countToNext.subtract(steps);
        }
    }
}
