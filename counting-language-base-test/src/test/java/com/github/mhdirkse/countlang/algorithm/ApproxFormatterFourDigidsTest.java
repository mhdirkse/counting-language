package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.math3.fraction.BigFraction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ApproxFormatterFourDigidsTest {
	@Parameters(name="{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{BigFraction.ZERO, "0"},
			{BigFraction.ONE, "1.000E0"},
			{BigFraction.ONE.negate(), "-1.000E0"},
			{new BigFraction(50, 1), "50.00E0"},
			{new BigFraction(300, 1), "300.0E0"},
			{new BigFraction(5000, 1), "5.000E3"},
			{new BigFraction(1234, 10000), "123.4E-3"},
			{new BigFraction(1234, 100000), "12.34E-3"},
			{new BigFraction(1234, 1000000), "1.234E-3"},
			{new BigFraction(1, 6), "166.7E-3"},
			{new BigFraction(16664, 1), "16.66E3"},
			{new BigFraction(16695, 1), "16.70E3"},
			{new BigFraction(16995, 1), "17.00E3"},
			{new BigFraction(19995, 1), "20.00E3"},
			{new BigFraction(99999, 1), "100.0E3"},
			// Very extreme numbers - this is why we created this slow algorithm
			{new BigFraction(123, 1).multiply(ApproxFormatter.TEN.pow(BigInteger.valueOf(1000))), "1.230E1002"},
			{new BigFraction(123, 1).multiply(ApproxFormatter.TEN.reciprocal().pow(1000)), "12.30E-999"}
		});
	}

	@Parameter(0)
	public BigFraction value;

	@Parameter(1)
	public String expectedRepresentation;

	private ApproxFormatter instance;

	@Before
	public void setUp() {
		instance = new ApproxFormatter();
	}

	@Test
	public void test() {
		assertEquals(expectedRepresentation, instance.format(value, 4));
	}
}
