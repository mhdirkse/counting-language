package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;

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
public class ApproxFormatterFiveDigidsTest {
	@Parameters(name="{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{BigFraction.ZERO, "0"},
			{BigFraction.ONE, "1.0000E0"},
			{BigFraction.ONE.negate(), "-1.0000E0"},
			{new BigFraction(50, 1), "50.000E0"},
			{new BigFraction(300, 1), "300.00E0"},
			{new BigFraction(5000, 1), "5.0000E3"},
			{new BigFraction(12345, 100000), "123.45E-3"},
			{new BigFraction(12345, 1000000), "12.345E-3"},
			{new BigFraction(12345, 10000000), "1.2345E-3"},
			{new BigFraction(1, 6), "166.67E-3"},
			{new BigFraction(166664, 1), "166.66E3"},
			{new BigFraction(166695, 1), "166.70E3"},
			{new BigFraction(166995, 1), "167.00E3"},
			{new BigFraction(199995, 1), "200.00E3"},
			{new BigFraction(999999, 1), "1.0000E6"}, 
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
		assertEquals(expectedRepresentation, instance.format(value, 5));
	}
}
