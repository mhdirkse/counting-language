package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionProbabilityOf extends AbstractMemberFunction {
	@SuppressWarnings("unchecked")
	public DistributionProbabilityOf() {
		super("probabilityOf", CountlangType.distributionOfAny(), t -> CountlangType.fraction(), t -> t.getSubType());
	}

	@Override
	public BigFraction run(int line, int column, List<Object> args) {
		Distribution thisArg = (Distribution) args.get(0);
		if(thisArg.getTotal().compareTo(BigInteger.ZERO) == 0) {
			throw new ProgramException(line, column, "Cannot calculate probabilities from empty distribution");
		}
		Object item = args.get(1);
		return new BigFraction(thisArg.getCountOf(item), thisArg.getTotal());
	}
}
