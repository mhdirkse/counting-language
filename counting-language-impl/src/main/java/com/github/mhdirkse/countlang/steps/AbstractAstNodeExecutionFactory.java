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

package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.Visitor;

abstract class AbstractAstNodeExecutionFactory implements AstNodeExecutionFactory, Visitor {
    AstNodeExecution result;

    @Override
    public AstNodeExecution create(AstNode node) {
        node.accept(this);
        AstNodeExecution currentResult = result;
        result = null;
        return currentResult;
    }

    @Override
    public void visitOperator(Operator operator) {
    }

    @Override
    public void visitFormalParameters(FormalParameters formalParameters) {
    }

    @Override
    public void visitFormalParameter(FormalParameter formalParameter) {
    }
}
