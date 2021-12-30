package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

public class FractionDenominator extends AbstractFractionToInt {
	public FractionDenominator() {
		super("denominator");
	}

	@Override
	BigInteger getResult(BigFraction arg) {
		return arg.getDenominator();
	}
}
