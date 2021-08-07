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

package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionFracE extends DistributionAggregator {
    private final DistributionFracSum delegate = new DistributionFracSum();

    public DistributionFracE() {
        super("E", CountlangType.distributionOf(CountlangType.fraction()), CountlangType.fraction());
    }

    @Override
    Object getInitialResult() {
        return delegate.getInitialResult();
    }

    @Override
    Object applyNext(Object original, Object item, BigInteger count) {
        return delegate.applyNext(original, item, count);
    }

    @Override
    Object finish(int line, int column, Distribution input, Object rawAggregate) {
        if(input.getTotal().compareTo(BigInteger.ZERO) == 0) {
            throw new ProgramException(line, column, "Division by zero");
        }
        BigFraction aggregate = (BigFraction) rawAggregate;
        return aggregate.divide(new BigFraction(input.getTotal()));
    }
}
