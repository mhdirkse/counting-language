package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionGetProbabilityOfUnknown extends DistributionGetNoArguments {
	public DistributionGetProbabilityOfUnknown() {
		super("probabilityOfUnknown", CountlangType.distributionOfAny());
	}

	@Override
	BigFraction getResult(int line, int column, Distribution distribution) {
		if(distribution.getTotal().compareTo(BigInteger.ZERO) == 0) {
			throw new ProgramException(line, column, "Cannot calculate probabilities from empty distribution");
		}
		return new BigFraction(distribution.getCountUnknown(), distribution.getTotal());
	}
}
