/*
 * Copyright Martijn Dirkse 2020
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

package com.github.mhdirkse.countlang.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mhdirkse.countlang.type.CountlangType;

public class FunctionDefinitions {
    private Map<FunctionKey, FunctionDefinition> functions = new HashMap<>();

    public boolean hasFunction(final FunctionKey key) {
        return getFunction(key) != null;
    }

    public FunctionDefinition getFunction(final FunctionKey key) {
        FunctionDefinition result = functions.get(key);
        if(result != null) {
            return result;
        }
        if(key.getOwnerType() == null) {
            return null;
        }
        List<CountlangType> generalizations = key.getOwnerType().getGeneralizations();
        for(CountlangType gen: generalizations) {
            FunctionKey genKey = new FunctionKey(key.getName(), gen);
            if(functions.containsKey(genKey)) {
                return functions.get(genKey);
            }
        }
        return null;
    }

    public void putFunction(final FunctionDefinition function) {
        functions.put(function.getKey(), function);
    }
}
