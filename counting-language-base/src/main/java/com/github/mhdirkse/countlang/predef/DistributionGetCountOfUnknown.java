package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;

import com.github.mhdirkse.countlang.algorithm.Distribution;

public class DistributionGetCountOfUnknown extends DistributionGetNoArguments {
	public DistributionGetCountOfUnknown() {
		super("countOfUnknown");
	}

	@Override
	BigInteger getResult(Distribution distribution) {
		return distribution.getCountUnknown();
	}
}
