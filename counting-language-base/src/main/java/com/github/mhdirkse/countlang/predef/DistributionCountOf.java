/*
 * Copyright Martijn Dirkse 2021
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

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionCountOf implements PredefinedFunction {

	@Override
	public FunctionKey getKey() {
		return new FunctionKey("countOf", CountlangType.distributionOfAny());
	}

	@Override
	public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
		if(arguments.size() != 2) {
			errorHandler.handleParameterCountMismatch(2, arguments.size());
			return CountlangType.unknown();
		}
		CountlangType thisArg = arguments.get(0);
		CountlangType arg = arguments.get(1);
		if(! thisArg.isDistribution()) {
			errorHandler.handleParameterTypeMismatch(0, CountlangType.distributionOfAny(), thisArg);
			return CountlangType.unknown();
		}
		if(arg != thisArg.getSubType()) {
			errorHandler.handleParameterTypeMismatch(1, thisArg.getSubType(), arg);
			return CountlangType.unknown();
		}
		return CountlangType.integer();
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		Distribution d = (Distribution) args.get(0);
		return d.getCountOf(args.get(1));
	}
}
