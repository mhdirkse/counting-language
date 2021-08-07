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
import lombok.Setter;

public final class SymbolExpression extends ExpressionNode {
    private final String symbol;

    @Getter
    @Setter
    private CountlangType countlangType = CountlangType.unknown();

    public SymbolExpression(final int line, final int column, String symbol) {
        super(line, column);
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitSymbolExpression(this);
    }
}
