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

import java.util.Arrays;
import java.util.List;

import com.github.mhdirkse.countlang.format.Format;

public final class PrintStatement extends Statement implements CompositeNode {
    private ExpressionNode expression = null;
    private Format format = Format.EXACT;

    public PrintStatement(final int line, final int column) {
        super(line, column);
    }

    public Format getFormat() {
    	return format;
    }

    public void setFormat(Format format) {
    	this.format = format;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public void setExpression(final ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitPrintStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return Arrays.asList(expression);
    }
}
