package com.github.mhdirkse.countlang.type;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractRange<T extends Comparable<T>> {
	T start;
	T endInclusive;
	T explicitStep;

	AbstractRange(List<T> components) {
		if((components.size() <= 1) || (components.size() >= 4)) {
			throw new IllegalArgumentException("Range has an invalid number of components");
		}
		start = components.get(0);
		endInclusive = components.get(1);
		if(components.size() == 3) {
			explicitStep = components.get(2);
		}
	}

	public boolean hasExplicitStep() {
		return explicitStep != null;
	}

	public List<T> enumerate() throws InvalidRangeException {
		T step = getStep();
		if(start.compareTo(endInclusive) < 0) {
			if(step.compareTo(getZero()) < 0) {
				throw new InvalidRangeException(toString());
			}
		}
		if(start.compareTo(endInclusive) > 0) {
			if(step.compareTo(getZero()) > 0) {
				throw new InvalidRangeException(toString());
			}
		} 
		if(step.equals(getZero())) {
			throw new InvalidRangeException(toString());
		}
		List<T> result = new ArrayList<>();
		if(start.compareTo(endInclusive) <= 0) {
			for(T current = start; current.compareTo(endInclusive) <= 0; current = add(current, step)) {
				result.add(current);
			}
		} else {
			for(T current = start; current.compareTo(endInclusive) >= 0; current = add(current, step)) {
				result.add(current);
			}			
		}
		return result;
	}

	private T getStep() {
		if(hasExplicitStep()) {
			return explicitStep;
		} else {
			return getOne();
		}
	}

	@Override
	public String toString() {
		return String.format("%s:%s:%s", start.toString(), getStep().toString(), endInclusive.toString());
	}

	abstract T getZero();
	abstract T getOne();
	abstract T add(T first, T second);
}
