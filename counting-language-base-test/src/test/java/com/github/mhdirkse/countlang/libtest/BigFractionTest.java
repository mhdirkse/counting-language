package com.github.mhdirkse.countlang.libtest;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
import org.junit.Test;

public class BigFractionTest {
	private static final BigInteger MINUS_SIX = new BigInteger("-6");
	private static final BigInteger MINUS_FOUR = new BigInteger("-4");
	private static final BigInteger THREE = new BigInteger("3");
	private static final BigInteger EIGHT = new BigInteger("8");

	@Test
	public void whenBigFractionCreatedWithNegativeDenThenNormalizedNumeratorNegative() {
		BigFraction b = new BigFraction(EIGHT, MINUS_SIX);
		assertEquals(MINUS_FOUR, b.getNumerator());
		assertEquals(THREE, b.getDenominator());
	}
}
