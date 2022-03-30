/*
 * Copyright Martijn Dirkse 2022
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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionProbabilityOfSet extends AbstractMemberFunction {
	private DistributionsStrategy strategy;

	public DistributionProbabilityOfSet() {
		super("probabilityOfAll", CountlangType.distributionOfAny(), t -> CountlangType.fraction(), t -> t);
		strategy = DistributionsStrategy.of(
				DistributionsStrategy.notEmpty(),
				DistributionsStrategy.otherIsSet("probabilityOfAll"));
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		strategy.test(line, column, args);
		BigInteger count = BigInteger.ZERO;
		Distribution thisArg = (Distribution) args.get(0);
		Distribution other = (Distribution) args.get(1);
		for(Iterator<Object> it = other.getItemIterator(); it.hasNext(); ) {
			Object item = it.next();
			count = count.add(thisArg.getCountOf(item));
		}
		return new BigFraction(count, thisArg.getTotal());
	}
}
