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

class Edge {
    private Samplable distribution;
    private BigInteger weight;
    private PossibilityValueIterator iterator;
    private ProbabilityTreeValue currentValue = null;

    Edge(Samplable distribution, BigInteger weight) {
        this.distribution = distribution;
        this.weight = weight;
        this.iterator = new PossibilityValueIterator(distribution);        
    }

    boolean hasNext() {
        return iterator.hasNext();
    }

    ProbabilityTreeValue next() {
        currentValue = iterator.next();
        return currentValue;
    }

    BigInteger getCount() {
        if(currentValue == null) {
            throw new IllegalStateException("Cannot give the count because no value has been selected");
        }
        return weight.multiply(distribution.getCountOf(currentValue));
    }

    void refine(BigInteger factor) {
        weight = weight.multiply(factor);
    }
}
