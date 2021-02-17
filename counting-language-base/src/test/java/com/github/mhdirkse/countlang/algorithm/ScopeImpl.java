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

package com.github.mhdirkse.countlang.algorithm;

class ScopeImpl implements Scope {
    private final ScopeAccess access;
    private final String symbol;

    ScopeImpl(ScopeAccess access) {
        this.access = access;
        symbol = null;
    }

    ScopeImpl(ScopeAccess access, String symbol) {
        this.access = access;
        this.symbol = symbol;
    }

    @Override
    public ScopeAccess getAccess() {
        return access;
    }

    @Override
    public boolean has(String testSymbol) {
        if(symbol == null) {
            return false;
        } else {
            return this.symbol.equals(testSymbol);
        }
    }
}
