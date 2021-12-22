package com.github.mhdirkse.countlang.type;

import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

public class FractionRange extends AbstractRange<BigFraction> {
	public FractionRange(List<BigFraction> components) {
		super(components);
	}

	@Override
	BigFraction getZero() {
		return BigFraction.ZERO;
	}

	@Override
	BigFraction getOne() {
		return BigFraction.ONE;
	}

	@Override
	BigFraction add(BigFraction first, BigFraction second) {
		return first.add(second);
	}
}
