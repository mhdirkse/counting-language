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

package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.algorithm.Scope;
import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.algorithm.ScopeAccess;
import com.github.mhdirkse.countlang.ast.ProgramException;

class ExecutionScope implements Scope {
    private final Map<String, Object> symbols;

    private final ScopeAccess access;

    public ExecutionScope(ScopeAccess access) {
        symbols = new HashMap<>();
        this.access = access;
    }

    ExecutionScope(ExecutionScope orig) {
        Map<String, Object> theSymbols = new HashMap<>();
        theSymbols.putAll(orig.symbols);
        this.symbols = theSymbols;
        this.access = orig.access;
    }

    @Override
    public ScopeAccess getAccess() {
        return access;
    }

    @Override
    public boolean has(String name) {
        return symbols.containsKey(name);
    }

    public Object read(String name, int line, int column) {
        if(symbols.containsKey(name)) {
            return symbols.get(name);
        }
        throw new ProgramException(line, column, "Undefined variable %s" + name);
    }

    public void write(String name, Object value, int line, int column) {
        symbols.put(name, value);
    }
}
