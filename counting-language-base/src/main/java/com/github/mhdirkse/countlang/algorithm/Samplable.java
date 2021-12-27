package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;
import java.util.Iterator;

public interface Samplable {
	BigInteger getTotal();
	Iterator<Object> getItemIterator();
	BigInteger getCountUnknown();
	BigInteger getCountOf(ProbabilityTreeValue value);

	public static Samplable multiple(Distribution source, BigInteger numSampled) {
		return new SequenceNoLayback(source, numSampled);
	}
}
