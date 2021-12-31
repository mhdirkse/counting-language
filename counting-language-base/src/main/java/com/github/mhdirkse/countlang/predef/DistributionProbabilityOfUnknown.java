package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionProbabilityOfUnknown extends AbstractMemberFunction {
	@SuppressWarnings("unchecked")
	public DistributionProbabilityOfUnknown() {
		super("probabilityOfUnknown", CountlangType.distributionOfAny(), t -> CountlangType.fraction());
	}

	@Override
	public BigFraction run(int line, int column, List<Object> values) {
		Distribution distribution = (Distribution) values.get(0);
		if(distribution.getTotal().compareTo(BigInteger.ZERO) == 0) {
			throw new ProgramException(line, column, "Cannot calculate probabilities from empty distribution");
		}
		return new BigFraction(distribution.getCountUnknown(), distribution.getTotal());
	}
}
