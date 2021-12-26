package com.github.mhdirkse.countlang.type;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.fraction.BigFraction;
import org.junit.Test;

public class RangeTest {
	@Test
	public void testIntRangeHappy_1() throws Exception {
		assertEquals("1, 2, 3", format(getIntRange(1, 3).enumerate()));
	}

	@Test
	public void testIntRangeHappy_2() throws Exception {
		assertEquals("1, 3", format(getIntRange(1, 3, 2).enumerate()));
	}

	@Test
	public void testIntRangeHappy_3() throws Exception {
		assertEquals("3, 2, 1", format(getIntRange(3, 1, -1).enumerate()));
	}

	@Test
	public void testIntRangeHappy_4() throws Exception {
		assertEquals("3, 1", format(getIntRange(3, 1, -2).enumerate()));
	}

	@Test(expected = InvalidRangeException.class)
	public void testInvalidRange_1() throws Exception {
		getIntRange(3, 1).enumerate();
	}

	@Test(expected = InvalidRangeException.class)
	public void testInvalidRange_2() throws Exception {
		getIntRange(3, 1, 2).enumerate();
	}

	@Test(expected = InvalidRangeException.class)
	public void testInvalidRange_3() throws Exception {
		getIntRange(1, 3, -2).enumerate();
	}

	@Test(expected = InvalidRangeException.class)
	public void testInvalidRange_4() throws Exception {
		getIntRange(1, 3, 0).enumerate();
	}

	@Test
	public void testFracRangeHappy_1() throws Exception {
		assertEquals("1 / 2, 3 / 2, 5 / 2", format(getFracRange(1, 2, 3, 1).enumerate()));
	}

	@Test
	public void testFracRangeHappy_2() throws Exception {
		assertEquals("5 / 2, 2, 3 / 2, 1, 1 / 2, 0", format(getFracRange(5, 2, 0, 1, -1, 2).enumerate()));
	}

	@Test(expected = InvalidRangeException.class)
	public void testFracRangeUnhappy() throws Exception {
		getFracRange(1, 2, 5, 2, 0, 1).enumerate();
	}

	@Test
	public void testDereferenceHappy() throws Exception {
		List<String> items = Arrays.asList("one", "two", "three");
		IntegerRange range = getIntRange(3, 1, -2);
		assertArrayEquals(new String[] {"three", "one"}, range.dereference(items).toArray(new String[] {}));
	}

	@Test
	public void testDereferenceStarOutOfBounds_1() throws Exception {
		try {
			IntegerRange range = getIntRange(0, 1);
			range.dereference(Arrays.asList("one", "two"));
		} catch(RangeIndexOutOfBoundsException e) {
			assertEquals(BigInteger.ZERO, e.getOffendingIndex());
		}
	}

	@Test
	public void testDereferenceStarOutOfBounds_2() throws Exception {
		try { 
			IntegerRange range = getIntRange(-1, 1);
			range.dereference(Arrays.asList("one", "two"));
		} catch(RangeIndexOutOfBoundsException e) {
			assertEquals(BigInteger.ONE.negate(), e.getOffendingIndex());
		}
	}

	@Test
	public void testDereferenceStarOutOfBounds_3() throws Exception {
		try {
			IntegerRange range = getIntRange(3, 1, -1);
			range.dereference(Arrays.asList("one", "two"));
		} catch(RangeIndexOutOfBoundsException e) {
			assertEquals(BigInteger.valueOf(3), e.getOffendingIndex());
		}
	}

	@Test
	public void testDereferenceEndOutOfBounds_1() throws Exception {
		try {
			IntegerRange range = getIntRange(1, 3);
			range.dereference(Arrays.asList("one", "two"));
		} catch(RangeIndexOutOfBoundsException e) {
			assertEquals(BigInteger.valueOf(3), e.getOffendingIndex());
		}
	}

	@Test
	public void testDereferenceEndOutOfBounds_2() throws Exception {
		try {
			IntegerRange range = getIntRange(1, 0, -1);
			range.dereference(Arrays.asList("one", "two"));
		}
		catch(RangeIndexOutOfBoundsException e) {
			assertEquals(BigInteger.ZERO, e.getOffendingIndex());
		}
	}

	@Test
	public void testDereferenceEndOutOfBounds_3() throws Exception {
		try {
			IntegerRange range = getIntRange(1, -1, -2);
			range.dereference(Arrays.asList("one", "two"));
		} catch(RangeIndexOutOfBoundsException e) {
			assertEquals(BigInteger.ONE.negate(), e.getOffendingIndex());
		}
	}

	private IntegerRange getIntRange(int start, int endInclusive) {
		return new IntegerRange(Arrays.asList(BigInteger.valueOf(start), BigInteger.valueOf(endInclusive)));
	}

	private IntegerRange getIntRange(int start, int endInclusive, int step) {
		return new IntegerRange(Arrays.asList(BigInteger.valueOf(start), BigInteger.valueOf(endInclusive), BigInteger.valueOf(step)));
	}

	private FractionRange getFracRange(int startNum, int startDen, int endNum, int endDen) {
		BigFraction start = new BigFraction(startNum, startDen);
		BigFraction endInclusive = new BigFraction(endNum, endDen);
		return new FractionRange(Arrays.asList(start, endInclusive));
	}

	private FractionRange getFracRange(int startNum, int startDen, int endNum, int endDen, int stepNum, int stepDen) {
		BigFraction start = new BigFraction(startNum, startDen);
		BigFraction endInclusive = new BigFraction(endNum, endDen);
		BigFraction step = new BigFraction(stepNum, stepDen);
		return new FractionRange(Arrays.asList(start, endInclusive, step));
	}

	private String format(List<? extends Object> items) {
		return items.stream().map(b -> b.toString()).collect(Collectors.joining(", "));
	}
}
