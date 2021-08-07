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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayExpression extends ExpressionNode implements CompositeNode {
    public ArrayExpression(int line, int column) {
        super(line, column);
    }

    private CountlangType countlangType = CountlangType.unknown();
    private List<ExpressionNode> elements = new ArrayList<>();

    @Override
    public CountlangType getCountlangType() {
        return countlangType;
    }

    public void setCountlangType(CountlangType countlangType) {
        this.countlangType = countlangType;
    }

    public void addElement(ExpressionNode element) {
        elements.add(element);
    }

    @Override
    public List<AstNode> getChildren() {
        return elements.stream().map(e -> (AstNode) e).collect(Collectors.toList());
    }

    @Override
    public void accept(Visitor v) {
        v.visitArrayExpression(this);
    }
}
