package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionCountOfUnknown extends AbstractDistributionNoArguments {
	public DistributionCountOfUnknown() {
		super("countOfUnknown", CountlangType.integer());
	}

	@Override
	BigInteger getResult(int line, int column, Distribution distribution) {
		return distribution.getCountUnknown();
	}
}
