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

import org.antlr.v4.runtime.misc.NotNull;

public class RepeatStatement extends Statement implements CompositeNode {
    private ExpressionNode countExpr;
    private StatementGroup statement;

    public RepeatStatement(int line, int column) {
        super(line, column);
    }

    public void setCountExpr(@NotNull final ExpressionNode countExpr) {
        if(countExpr == null) {
            throw new NullPointerException("Test expression of while-statement should not be null");
        }
        this.countExpr = countExpr;
    }

    public ExpressionNode getCountExpr() {
        return countExpr;
    }

    public void setStatement(@NotNull final StatementGroup statement) {
        if(statement == null) {
            throw new NullPointerException("Statement group of while-statement should not be null");
        }
        this.statement = statement;
    }

    public StatementGroup getStatement() {
        return statement;
    }

    @Override
    public void accept(Visitor v) {
        v.visitRepeatStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(countExpr);
        result.add(statement);
        return result;
    }
}
