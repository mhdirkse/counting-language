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

package com.github.mhdirkse.countlang.format;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.ApproxFormatter;
import com.github.mhdirkse.countlang.algorithm.Distribution;

class Approximate extends CommonFormat {
	private static final int NUM_DIGIDS = 4;

	@Override
	String formatCountInDistributionTable(Object item, Distribution d) {
		return formatCountAndProbability(d.getCountOf(item), new BigFraction(d.getCountOf(item), d.getTotal()));
	}

	@Override
	String formatCountUnknownInDistributionTable(Distribution d) {
		return formatCountAndProbability(d.getCountUnknown(), new BigFraction(d.getCountUnknown(), d.getTotal()));
	}

	String formatTotalInDistributionTable(Distribution d) {
		BigFraction totalAsFraction = new BigFraction(d.getTotal(), BigInteger.ONE);
		String totalStr = ApproxFormatter.getInstance().format(totalAsFraction, NUM_DIGIDS);
		return String.format("%s (1)", totalStr);
	}

	String formatCountAndProbability(BigInteger count, BigFraction probability) {
		BigFraction countAsFrac = new BigFraction(count, BigInteger.ONE);
		String countStr = ApproxFormatter.getInstance().format(countAsFrac, NUM_DIGIDS);
		String probStr = ApproxFormatter.getInstance().format(probability, NUM_DIGIDS);
		return String.format("%s (%s)", countStr, probStr);
	}

	@Override
	String formatBigFraction(BigFraction b) {
		return ApproxFormatter.getInstance().format(b, NUM_DIGIDS);
	}
}
