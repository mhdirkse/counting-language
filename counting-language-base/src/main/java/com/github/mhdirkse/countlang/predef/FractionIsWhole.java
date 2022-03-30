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

package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangType;

public class FractionIsWhole implements PredefinedFunction {
	@Override
	public FunctionKey getKey() {
		return new FunctionKey("isWhole");
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 1) {
			errorHandler.handleParameterCountMismatch(1, arguments.size());
			return CountlangType.unknown();
		}
		if(arguments.get(0) != CountlangType.fraction()) {
			errorHandler.handleParameterTypeMismatch(0, CountlangType.fraction(), arguments.get(0));
			return CountlangType.unknown();
		}
		return CountlangType.bool();
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		BigFraction arg = (BigFraction) args.get(0);
		return arg.getDenominator().equals(BigInteger.ONE);
	}
}
