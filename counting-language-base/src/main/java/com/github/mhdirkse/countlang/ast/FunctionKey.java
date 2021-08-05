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

package com.github.mhdirkse.countlang.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public final class FunctionKey {
    @Getter
    private final String name;

    @Getter
    private final CountlangType ownerType;

    /**
     * Create key of function that is not a member.
     */
    public FunctionKey(String name) {
        this.name = name;
        this.ownerType = null;
    }

    /**
     * Create key of a member function.
     */
    public FunctionKey(String name, CountlangType ownerType) {
        this.name = name;
        this.ownerType = ownerType;
    }

    @Override
    public String toString() {
        if(ownerType == null) {
            return name;
        } else {
            return String.format("%s.%s", ownerType.toString(), name);
        }
    }
}
