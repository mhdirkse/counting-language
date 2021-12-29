package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;
import java.util.Iterator;

public interface Samplable {
	public static final BigInteger MAX_NUM_SAMPLED = BigInteger.valueOf(10*1000*1000L);

	BigInteger getTotal();
	Iterator<Object> getItemIterator();
	BigInteger getCountUnknown();
	BigInteger getCountOf(ProbabilityTreeValue value);

	public static Samplable multiple(Distribution source, BigInteger numSampled) {
		return new SequenceLayback(source, numSampled);
	}
}
