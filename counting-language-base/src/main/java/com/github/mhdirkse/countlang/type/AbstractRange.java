/*
 * Copyright Martijn Dirkse 2022
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.type;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRange<T extends Comparable<T>> {
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
