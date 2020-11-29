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

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public final class CompositeExpression extends ExpressionNode implements CompositeNode {
    private Operator operator = null;
    private List<ExpressionNode> subExpressions = new ArrayList<>();

    @Getter
    @Setter
    private CountlangType countlangType;

    public CompositeExpression(final int line, final int column) {
        super(line, column);
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    public int getNumSubExpressions() {
        return subExpressions.size();
    }

    public ExpressionNode getSubExpression(final int index) {
        return subExpressions.get(index);
    }

    public void addSubExpression(final ExpressionNode expression) {
        subExpressions.add(expression);
    }

    @Override
    public void accept(final Visitor v) {
        v.visitCompositeExpression(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(operator);
        result.addAll(subExpressions);
        return result;
    }
}
