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

package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;
import java.util.Iterator;

public interface Samplable {
	public static final BigInteger MAX_NUM_SAMPLED = BigInteger.valueOf(10*1000*1000L);

	BigInteger getTotal();
	Iterator<Object> getItemIterator();
	BigInteger getCountUnknown();
	BigInteger getCountOf(ProbabilityTreeValue value);

	public static Samplable multiple(Distribution source, BigInteger numSampled) {
		return new SequenceLayback(source, numSampled);
	}
}
