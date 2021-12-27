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

import com.github.mhdirkse.countlang.utils.Stack;

/**
 * Walks a probability tree as explained with {@link SampleContext}.
 * @author martijn
 *
 */
class PossibilitiesWalker {
    private BigInteger total = BigInteger.ONE;
    private Stack<Edge> edges = new Stack<>();

    PossibilitiesWalker() {
    }

    int getNumEdges() {
        return edges.size();
    }

    void down(Samplable distribution) {
        if(distribution.getTotal().compareTo(BigInteger.ZERO) == 0) {
            throw new IllegalArgumentException("Cannot iterate over an empty distribution");
        }
        if(getCount().mod(distribution.getTotal()).compareTo(BigInteger.ZERO) != 0) {
            throw new IllegalArgumentException(String.format("Cannot fit distribution with total %d in count %d", distribution.getTotal(), total));
        }
        edges.push(new Edge(distribution, getCount().divide(distribution.getTotal())));
    }

    void up() {
        edges.pop();
    }

    boolean hasNext() {
        return (edges.size() >= 1) && edges.peek().hasNext();
    }

    ProbabilityTreeValue next() {
        return edges.peek().next();
    }

    BigInteger getCount() {
        if(edges.isEmpty()) {
            return total;
        } else {
            return edges.peek().getCount();
        }
    }

    BigInteger getTotal() {
        return total;
    }

    void refine(BigInteger factor) {
        total = total.multiply(factor);
        edges.forEach(e -> e.refine(factor));        
    }
}
