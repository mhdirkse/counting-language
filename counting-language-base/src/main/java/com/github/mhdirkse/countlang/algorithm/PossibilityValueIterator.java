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
import java.util.Iterator;

class PossibilityValueIterator implements Iterator<ProbabilityTreeValue> {
    private final Iterator<Object> normalValueIterator;
    private final boolean distributionIncludesUnknown;
    private boolean returnedUnknown = false;

    PossibilityValueIterator(Samplable subject) {
        normalValueIterator = subject.getItemIterator();
        distributionIncludesUnknown = subject.getCountUnknown().compareTo(BigInteger.ZERO) > 0;
    }

    @Override
    public boolean hasNext() {
        boolean returnStillToReturn = distributionIncludesUnknown && (! returnedUnknown);
        return normalValueIterator.hasNext() || returnStillToReturn;
    }

    @Override
    public ProbabilityTreeValue next() {
        if(! hasNext()) {
            throw new IllegalArgumentException("PossibilityValueIterator has no more values");
        }
        if(normalValueIterator.hasNext()) {
            return ProbabilityTreeValue.of(normalValueIterator.next());
        }
        returnedUnknown = true;
        return ProbabilityTreeValue.unknown();
    }
}
