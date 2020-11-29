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

import org.antlr.v4.runtime.misc.NotNull;

public class IfStatement extends Statement implements CompositeNode {
    private ExpressionNode selector;
    private StatementGroup thenStatement;
    private StatementGroup elseStatement;

    public IfStatement(final int line, final int column) {
        super(line, column);
    }
    
    public void setSelector(@NotNull final ExpressionNode selector) {
        if(selector == null) {
            throw new NullPointerException("Selector of if-statement should not be null");
        }
        this.selector = selector;
    }

    public ExpressionNode getSelector() {
        return selector;
    }

    public void setThenStatement(@NotNull final StatementGroup thenStatement) {
        if(thenStatement == null) {
            throw new NullPointerException("Then statement of if-statement should not be null");
        }
        this.thenStatement = thenStatement;
    }

    public StatementGroup getThenStatement() {
        return thenStatement;
    }

    public void setElseStatement(final StatementGroup elseStatement) {
        this.elseStatement = elseStatement;
    }

    public StatementGroup getElseStatement() {
        return elseStatement;
    }

    @Override
    public void accept(Visitor v) {
        v.visitIfStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>(3);
        result.add(selector);
        result.add(thenStatement);
        if(elseStatement != null) {
            result.add(elseStatement);
        }
        return result;
    }
}
