package com.github.mhdirkse.countlang.type;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class IntegerRange extends AbstractRange<BigInteger> {
	public IntegerRange(List<BigInteger> components) {
		super(components);
	}

	/**
	 * Dereferences a list according to a range, returning the selected items. Indexes start from 1.
	 * @throws {@link IndexOutOfBoundsException} if the start or the end index is outside the list.
	 * @throws InvalidRangeException if the start, the end and the step are not compatible with each other (e.g. 1:-1:2).
	 */
	public <T> List<? super T> dereference(List<T> items) throws InvalidRangeException {
		BigInteger numItems = BigInteger.valueOf(items.size());
		if( (start.compareTo(BigInteger.ZERO) <= 0) || (endInclusive.compareTo(BigInteger.ZERO) <= 0) ||
				(start.compareTo(numItems) > 0) || (endInclusive.compareTo(numItems) > 0)) {
			throw new IndexOutOfBoundsException();
		}
		return enumerate().stream()
				.map(b -> b.intValue())
				.map(i -> i - 1)
				.map(i -> items.get(i))
				.collect(Collectors.toList());
	}

	@Override
	BigInteger getZero() {
		return BigInteger.ZERO;
	}

	@Override
	BigInteger getOne() {
		return BigInteger.ONE;
	}

	@Override
	BigInteger add(BigInteger first, BigInteger second) {
		return first.add(second);
	}
}
