package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

public class FractionNumerator extends AbstractFractionToInt {
	public FractionNumerator() {
		super("numerator");
	}

	@Override
	BigInteger getResult(BigFraction arg) {
		return arg.getNumerator();
	}
}
