package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionSize extends AbstractDistributionNoArguments {
	public DistributionSize() {
		super("size", CountlangType.integer());
	}

	@Override
	BigInteger getResult(int line, int column, Distribution distribution) {
		return distribution.getTotal();
	}
}
