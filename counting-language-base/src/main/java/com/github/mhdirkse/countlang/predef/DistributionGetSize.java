package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;

import com.github.mhdirkse.countlang.algorithm.Distribution;

public class DistributionGetSize extends DistributionGetNoArguments {
	public DistributionGetSize() {
		super("size");
	}

	@Override
	BigInteger getResult(Distribution distribution) {
		return distribution.getTotal();
	}
}
