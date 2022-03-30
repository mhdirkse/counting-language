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

package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayTypeNode extends CompositeTypeNode {
    public ArrayTypeNode(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getCountlangType() {
        return CountlangType.arrayOf(children.get(0).getCountlangType());
    }

    @Override
    public void accept(Visitor v) {
        v.visitArrayTypeNode(this);
    }
}
