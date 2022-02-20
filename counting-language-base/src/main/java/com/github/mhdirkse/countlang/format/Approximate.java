package com.github.mhdirkse.countlang.format;

import java.math.BigInteger;
import java.text.DecimalFormat;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;

class Approximate extends CommonFormat {
	private final DecimalFormat engineeringFormatter = new DecimalFormat("##0.000E0");

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
		String totalStr = engineeringFormatter.format(totalAsFraction.doubleValue());
		return String.format("%s (1)", totalStr);
	}

	String formatCountAndProbability(BigInteger count, BigFraction probability) {
		BigFraction countAsFrac = new BigFraction(count, BigInteger.ONE);
		String countStr = engineeringFormatter.format(countAsFrac.doubleValue());
		String probStr = engineeringFormatter.format(probability.doubleValue());
		return String.format("%s (%s)", countStr, probStr);
	}

	@Override
	String formatBigFraction(BigFraction b) {
		return engineeringFormatter.format(b.doubleValue());
	}

}
