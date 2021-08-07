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

import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.type.CountlangType;

abstract class DistributionMemberNoArguments implements PredefinedFunction {
    private final String name;
    private final CountlangType ownerType;
    private final CountlangType returnType;

    DistributionMemberNoArguments(String name, CountlangType ownerType, CountlangType returnType) {
        this.name = name;
        this.ownerType = ownerType;
        this.returnType = returnType;
    }

    @Override
    public FunctionKey getKey() {
        return new FunctionKey(name, ownerType);
    }

    @Override
    public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
        if(arguments.size() != 1) {
            errorHandler.handleParameterCountMismatch(1, arguments.size());
            return null;
        }
        CountlangType thisArg = arguments.get(0);
        if(thisArg != ownerType) {
            errorHandler.handleParameterTypeMismatch(1, ownerType, thisArg);
            return null;
        }
        return returnType;
    }
}
