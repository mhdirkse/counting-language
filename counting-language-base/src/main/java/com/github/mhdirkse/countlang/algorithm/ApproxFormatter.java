package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

public class ApproxFormatter {
	private static ApproxFormatter instance;
	static final BigFraction TEN = new BigFraction(10, 1);
	private static final BigInteger INT_TEN = new BigInteger("10");

	public static ApproxFormatter getInstance() {
		if(instance == null) {
			instance = new ApproxFormatter();
		}
		return instance;
	}

	/**
	 * package-private constructor. In production, this class should be a singleton.
	 * In unit tests, we want to control the instance we use.
	 */
	ApproxFormatter() {
	}

	public String format(BigFraction number, int numDigids) {
		return (new FormattingCalculation(numDigids)).format(number);
	}

	private static class FormattingCalculation {
		private final int numDigids;
		private boolean isNegative;
		private int exponentOffset = 0;
		private BigFraction absNumber;
		private int exponent;
		private List<Integer> digids;

		FormattingCalculation(int numDigids) {
			if(numDigids < 4) {
				throw new IllegalArgumentException("Cannot format with less than 4 digids");
			}
			this.numDigids = numDigids;
		}

		String format(BigFraction number) {
			if(number.equals(BigFraction.ZERO)) {
				return "0";
			}
			isNegative = (number.compareTo(BigFraction.ZERO) < 0);
			if(isNegative) {
				absNumber = number.negate();
			} else {
				absNumber = number;
			}
			String formattedAbsValue = formatAbsolute();
			if(isNegative) {
				return "-" + formattedAbsValue;
			} else {
				return formattedAbsValue;
			}
		}

		String formatAbsolute() {
			multiplyByTenUntilEnoughDigids();
			calculateDigids();
			if(firstOmittedDigid() >= 5) {
				roundUpTheDigids();
			}
			return formatTheDigids();
		}

		private Integer firstOmittedDigid() {
			return digids.get(numDigids);
		}

		private void multiplyByTenUntilEnoughDigids() {
			// We need one extra digid to properly round.
			BigFraction threshold = TEN.pow(BigInteger.valueOf(numDigids + 1));
			while(absNumber.compareTo(threshold) < 0) {
				absNumber = absNumber.multiply(TEN);
				--exponentOffset;
			}
		}

		private void calculateDigids() {
			BigInteger integerPart = absNumber.getNumerator().divide(absNumber.getDenominator());
			digids = new ArrayList<>();
			while(integerPart.compareTo(BigInteger.ZERO) > 0) {
				BigInteger[] divRem = integerPart.divideAndRemainder(INT_TEN);
				digids.add(divRem[1].intValue());
				integerPart = divRem[0];
			}
			Collections.reverse(digids);
			// When there are two digids, say 50, then the exponent is 1: It is 5E1.
			int digidsExponent = digids.size() - 1;
			exponent = digidsExponent + exponentOffset;
		}

		private void roundUpTheDigids() {
			boolean firstOrCarry = true;
			for(int i = (numDigids - 1); firstOrCarry && (i >= 0); --i) {
				if(firstOrCarry) {
					int newDigid = digids.get(i) + 1;
					firstOrCarry = false;
					if(newDigid == 10) {
						newDigid = 0;
						firstOrCarry = true;
					}
					digids.set(i, newDigid);
				} else {
					break;
				}
			}
			if(firstOrCarry) {
				digids.add(0, 1);
				++exponent;
			}
		}

		private String formatTheDigids() {
			int exponentModulus = Math.floorMod(exponent, 3);
			int shownExponent = exponent - exponentModulus;
			int numDigidsBeforeDot = 1 + exponentModulus;
			StringBuilder result = new StringBuilder();
			for(int i = 0; i < numDigids; ++i) {
				if(i == numDigidsBeforeDot) {
					result.append(".");
				}
				result.append(Integer.valueOf(digids.get(i).toString()));
			}
			result.append(String.format("E%d", shownExponent));
			return result.toString();
		}
	}
}
