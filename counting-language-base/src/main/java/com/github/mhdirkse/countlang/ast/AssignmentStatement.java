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

public final class AssignmentStatement extends Statement implements CompositeNode {
    private String lhs = null;
    private ExpressionNode rhs = null;

    public AssignmentStatement(final int line, final int column) {
        super(line, column);
    }

    public String getLhs() {
        return lhs;
    }

    public void setLhs(final String lhs) {
        this.lhs = lhs;
    }

    public ExpressionNode getRhs() {
        return rhs;
    }

    public void setRhs(final ExpressionNode rhs) {
        this.rhs = rhs;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitAssignmentStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return Arrays.asList(rhs);
    }
}
