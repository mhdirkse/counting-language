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

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionIntSum extends DistributionAggregator {
    public DistributionIntSum() {
        super("sum", CountlangType.distributionOf(CountlangType.integer()), CountlangType.integer(),
        		DistributionsStrategy.noUnknown("sum"));
    }

    @Override
    Object getInitialResult() {
        return BigInteger.ZERO;
    }

    @Override
    Object applyNext(Object rawOriginal, Object rawItem, BigInteger count) {
        BigInteger original = (BigInteger) rawOriginal;
        BigInteger item = (BigInteger) rawItem;
        BigInteger toAdd = item.multiply(count);
        return original.add(toAdd);
    }

    @Override
    Object finish(int line, int column, Distribution input, Object aggregate) {
        return aggregate;
    }
}
