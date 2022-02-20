package com.github.mhdirkse.countlang.format;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;

class Exact extends CommonFormat {
	@Override
	String formatCountInDistributionTable(Object item, Distribution d) {
		return d.getCountOf(item).toString();
	}

	@Override
	String formatCountUnknownInDistributionTable(Distribution d) {
		return d.getCountUnknown().toString();
	}

	@Override
	String formatTotalInDistributionTable(Distribution d) {
		return d.getTotal().toString();
	}

    @Override
	String formatBigFraction(BigFraction b) {
        if(b.compareTo(BigFraction.ZERO) == 0) {
            return "0";
        }
        BigFraction v = b;
        String firstSign = "";
        String secondSign = "+";
        if(b.compareTo(BigFraction.ZERO) < 0) {
            v = b.negate();
            firstSign = "-";
            secondSign = "-";
        }
        BigInteger[] divAndRem = v.getNumerator().divideAndRemainder(v.getDenominator());
        BigInteger div = divAndRem[0];
        BigInteger rem = divAndRem[1];
        if(div.compareTo(BigInteger.ZERO) == 0) {
            return String.format("%s%s / %s", firstSign, rem.toString(), v.getDenominator().toString());
        } else if(rem.compareTo(BigInteger.ZERO) == 0) {
            return String.format("%s%s", firstSign, div.toString());
        } else {
            return String.format("%s%s %s %s / %s", firstSign, div.toString(), secondSign, rem.toString(), v.getDenominator().toString());
        }
    }
}
