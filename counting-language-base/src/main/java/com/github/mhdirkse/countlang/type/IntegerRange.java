package com.github.mhdirkse.countlang.type;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class IntegerRange extends AbstractRange<BigInteger> {
	public IntegerRange(List<BigInteger> components) {
		super(components);
	}

	public <T> List<T> dereference(List<T> items) throws InvalidRangeException, RangeIndexOutOfBoundsException {
		BigInteger numItems = BigInteger.valueOf(items.size());
		if( (start.compareTo(BigInteger.ZERO) <= 0) || (start.compareTo(numItems) > 0)) {
			throw new RangeIndexOutOfBoundsException(start);
		}
		if( (endInclusive.compareTo(BigInteger.ZERO) <= 0) || (endInclusive.compareTo(numItems) > 0)) {
			throw new RangeIndexOutOfBoundsException(endInclusive);
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
