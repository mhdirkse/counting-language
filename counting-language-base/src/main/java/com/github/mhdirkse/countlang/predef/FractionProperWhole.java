package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

public class FractionProperWhole extends AbstractFractionToInt {
	public FractionProperWhole() {
		super("properWhole");
	}

	@Override
	BigInteger getResult(BigFraction arg) {
		BigInteger num = arg.getNumerator();
		BigInteger den = arg.getDenominator();
		return num.divide(den);
	}
}
