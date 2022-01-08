package com.github.mhdirkse.countlang.type;

import static org.junit.Assert.assertArrayEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class CountlangArrayTest {
	@Test
	public void testGetSortRefs() {
		List<Object> items = Arrays.asList("20", "40", "30", "10");
		CountlangArray instance = new CountlangArray(items);
		List<BigInteger> sortRefs = instance.getSortRefs();
		List<Integer> actual = sortRefs.stream().map(b -> b.intValue()).collect(Collectors.toList());
		List<Object> shouldBeSorted = actual.stream().map(i -> items.get(i-1)).collect(Collectors.toList());
		assertArrayEquals(new String[] {"10", "20", "30", "40"}, shouldBeSorted.toArray(new String[] {}));
	}
}
