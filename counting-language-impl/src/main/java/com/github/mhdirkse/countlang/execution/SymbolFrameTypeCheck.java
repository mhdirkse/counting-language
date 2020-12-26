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

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.CountlangType;

/**
 * Implements a {@link SymbolFrame} for two purposes. First, checking
 * whether variables exist. Second, checking that each write to
 * a variable is done with the same {@link com.github.mhdirkse.countlang.ast.CountlangType}.
 * 
 * Every branch of an if-statement opens a new block scope. Therefore, we assume that
 * no new symbols are added while any switch statement is opened. Within a branch,
 * the same symbols exist in this frame as existed before the switch statement
 * was started. Therefore we do not have to distinguish branches to check
 * whether a symbol exists.
 * 
 * @author martijn
 *
 */
class SymbolFrameTypeCheck implements SymbolFrame<CountlangType>{
    private final Map<String, CountlangType> symbols = new HashMap<>();

    private final SymbolNotAccessibleHandler handler;
    private final StackFrameAccess access;
    
    private int numSwitchOpen = 0;
    
    SymbolFrameTypeCheck(StackFrameAccess access, final SymbolNotAccessibleHandler handler) {
        this.handler = handler;
        this.access = access;
    }

    @Override
    public boolean has(String name) {
        return symbols.containsKey(name);
    }

    @Override
    public CountlangType read(String name, int line, int column) {
        if(!symbols.containsKey(name)) {
            handler.notReadable(name, line, column);
            return CountlangType.UNKNOWN;
        }
        return symbols.get(name);
    }

    @Override
    public void write(String name, CountlangType value, int line, int column) {
        if(symbols.containsKey(name)) {
            if(!symbols.get(name).equals(value)) {
                handler.notWritable(name, line, column);
            }
        }
        else if(numSwitchOpen >= 1) {
            throw new IllegalStateException("Expected no new symbol within a switch statement: " + name);
        }
        else {
            symbols.put(name, value);
        }
    }

    @Override
    public StackFrameAccess getAccess() {
        return access;
    }

    @Override
    public void onSwitchOpened() {
        numSwitchOpen++;
    }

    @Override
    public void onSwitchClosed() {
        numSwitchOpen--;
    }

    @Override
    public void onBranchOpened() {
    }

    @Override
    public void onBranchClosed() {
    }

    @Override
    public void onRepetitionOpened() {
    }

    @Override
    public void onRepetitionClosed() {
    }
}
