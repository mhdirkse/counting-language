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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class FunctionCallExpression extends ExpressionNode implements CompositeNode {
    @Getter(AccessLevel.PACKAGE)
    @Setter
    private String name;

    @Getter
    @Setter
    private CountlangType countlangType;
    
    private List<ExpressionNode> arguments = new ArrayList<>();

    public FunctionCallExpression(final int line, final int column) {
        super(line, column);
    }

    public abstract FunctionKey getKey();

    public int getNumArguments() {
        return arguments.size();
    }

    public ExpressionNode getArgument(int i) {
        return arguments.get(i);
    }

    public void addArgument(final ExpressionNode expression) {
        arguments.add(expression);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.addAll(arguments);
        return result;
    }
}
