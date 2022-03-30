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
