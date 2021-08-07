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

package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ArrayExpression;
import com.github.mhdirkse.countlang.ast.ExpressionNode;

public class ArrayExpressionHandler extends AbstractExpressionHandler implements ExpressionSource {
    private ArrayExpression result;

    public ArrayExpressionHandler(int line, int column) {
        result = new ArrayExpression(line, column);
    }

    @Override
    void addExpression(ExpressionNode expression) {
        result.addElement(expression);
    }

    @Override
    public ExpressionNode getExpression() {
        return result;
    }
}
