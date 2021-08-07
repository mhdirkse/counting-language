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

import com.github.mhdirkse.countlang.type.CountlangType;

import lombok.Getter;

public class FormalParameter extends AstNode {
    @Getter
    private final String name;

    @Getter
    private final CountlangType countlangType;

    public FormalParameter(final int line, final int column, final String name, CountlangType countlangType) {
        super(line, column);
        this.name = name;
        this.countlangType = countlangType;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitFormalParameter(this);
    }
}
