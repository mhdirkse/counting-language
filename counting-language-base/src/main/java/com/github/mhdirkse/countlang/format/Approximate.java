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
