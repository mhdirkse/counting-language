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

import com.github.mhdirkse.countlang.ast.CountlangType;

final class Symbol {
    private String name;
    private CountlangType countlangType;
    private Object value;

    public Symbol(String name) {
        this.name = name;
        this.countlangType = CountlangType.UNKNOWN;
        this.value = null;
    }

    public String getName() {
        return name;
    }

    public CountlangType getCountlangType() {
        return countlangType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if(value == null) {
            throw new NullPointerException();
        }
        CountlangType newCountlangType = CountlangType.typeOf(value);
        if(countlangType == CountlangType.UNKNOWN) {
            this.countlangType = CountlangType.typeOf(value);
        } else if(newCountlangType != countlangType) {
            throw new IllegalArgumentException(String.format(
                    "Attempt to change type of symbol %s from %s to %s",
                    name, countlangType.toString(), newCountlangType.toString()));
        }
        this.value = value;
    }
}