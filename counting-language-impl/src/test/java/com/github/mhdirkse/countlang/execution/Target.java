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

import java.math.BigInteger;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.type.CountlangType;

class Target {
    static CompositeExpression getCompositeExpression() {
        ValueExpression firstOperand = new ValueExpression(1, 1, new BigInteger("5"), CountlangType.integer());
        SymbolExpression secondOperand = new SymbolExpression(1, 3, "x");
        Operator operatorAdd = new Operator.OperatorAdd(1, 2);
        CompositeExpression target = new CompositeExpression(1, 1);
        target.setOperator(operatorAdd);
        target.addSubExpression(firstOperand);
        target.addSubExpression(secondOperand);
        return target;
    }
}
