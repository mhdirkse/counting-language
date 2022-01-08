package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionIsSet extends AbstractMemberFunction {
	public DistributionIsSet() {
		super("isSet", CountlangType.distributionOfAny(), t -> CountlangType.bool());
	}

	@Override
	public Boolean run(int line, int column, List<Object> args) {
		return ((Distribution) args.get(0)).isSet();
	}
}
