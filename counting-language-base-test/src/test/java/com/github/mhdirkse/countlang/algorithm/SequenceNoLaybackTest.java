package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import com.github.mhdirkse.countlang.type.CountlangArray;

public class SequenceNoLaybackTest {
	@Test
	public void testSampleTwoWithoutUnknown() {
		Distribution.Builder b = new Distribution.Builder();
		b.add(BigInteger.valueOf(4L), BigInteger.valueOf(2L));
		b.add(BigInteger.valueOf(5L), BigInteger.valueOf(3L));
		Distribution d = b.build();
		Samplable instance = new SequenceNoLayback(d, BigInteger.valueOf(2L));
		assertEquals(0, instance.getCountUnknown().compareTo(BigInteger.ZERO));
		assertEquals(0, instance.getCountOf(ProbabilityTreeValue.unknown()).compareTo(BigInteger.ZERO));
		assertEquals(25L, instance.getTotal().longValue());
		CountlangArray first = new CountlangArray(Arrays.asList(BigInteger.valueOf(4L), BigInteger.valueOf(4L)));
		CountlangArray second = new CountlangArray(Arrays.asList(BigInteger.valueOf(4L), BigInteger.valueOf(5L)));
		CountlangArray third = new CountlangArray(Arrays.asList(BigInteger.valueOf(5L), BigInteger.valueOf(4L)));
		CountlangArray fourth = new CountlangArray(Arrays.asList(BigInteger.valueOf(5L), BigInteger.valueOf(5L)));
		Iterator<Object> it = instance.getItemIterator();
		assertTrue(it.hasNext());
		Object retrieved = it.next();
		assertEquals(first, retrieved);
		assertEquals(4L, instance.getCountOf(ProbabilityTreeValue.of(first)).longValue());
		assertEquals(4L, instance.getCountOf(ProbabilityTreeValue.of(retrieved)).longValue());
		assertTrue(it.hasNext());
		assertEquals(second, it.next());
		assertEquals(6L, instance.getCountOf(ProbabilityTreeValue.of(second)).longValue());
		assertTrue(it.hasNext());
		assertEquals(third, it.next());
		assertEquals(6L, instance.getCountOf(ProbabilityTreeValue.of(third)).longValue());
		assertTrue(it.hasNext());
		assertEquals(fourth, it.next());
		assertEquals(9L, instance.getCountOf(ProbabilityTreeValue.of(fourth)).longValue());
		assertFalse(it.hasNext());
	}

	@Test
	public void testSampleTwoOnlyUnknown() {
		Distribution.Builder b = new Distribution.Builder();
		b.addUnknown(BigInteger.valueOf(5L));
		Distribution d = b.build();
		Samplable instance = new SequenceNoLayback(d, BigInteger.valueOf(2L));
		assertEquals(25L, instance.getTotal().longValue());
		assertEquals(25L, instance.getCountUnknown().longValue());
		assertEquals(25L, instance.getCountOf(ProbabilityTreeValue.unknown()).longValue());
		assertFalse(instance.getItemIterator().hasNext());
	}
}
