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

package com.github.mhdirkse.countlang.type;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class IntegerRange extends AbstractRange<BigInteger> {
	public IntegerRange(List<BigInteger> components) {
		super(components);
	}

	public <T> List<T> dereference(List<T> items) throws InvalidRangeException, RangeIndexOutOfBoundsException {
		BigInteger numItems = BigInteger.valueOf(items.size());
		if( (start.compareTo(BigInteger.ZERO) <= 0) || (start.compareTo(numItems) > 0)) {
			throw new RangeIndexOutOfBoundsException(start);
		}
		if( (endInclusive.compareTo(BigInteger.ZERO) <= 0) || (endInclusive.compareTo(numItems) > 0)) {
			throw new RangeIndexOutOfBoundsException(endInclusive);
		}
		return enumerate().stream()
				.map(b -> b.intValue())
				.map(i -> i - 1)
				.map(i -> items.get(i))
				.collect(Collectors.toList());
	}

	@Override
	BigInteger getZero() {
		return BigInteger.ZERO;
	}

	@Override
	BigInteger getOne() {
		return BigInteger.ONE;
	}

	@Override
	BigInteger add(BigInteger first, BigInteger second) {
		return first.add(second);
	}
}
