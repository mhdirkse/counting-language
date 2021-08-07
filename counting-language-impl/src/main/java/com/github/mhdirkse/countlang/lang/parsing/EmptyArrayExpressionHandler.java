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

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ArrayExpression;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.lang.CountlangParser;
import com.github.mhdirkse.countlang.type.CountlangType;

public class EmptyArrayExpressionHandler extends AbstractCountlangListenerHandler implements ExpressionSource {
    private ArrayExpression expression;
    private TypeIdHandler typeHandler;

    public EmptyArrayExpressionHandler(int line, int column) {
        super(false);
        typeHandler = new TypeIdHandler(line, column);
        expression = new ArrayExpression(line, column);
    }

    @Override
    public boolean enterSimpleType(
            CountlangParser.SimpleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.enterSimpleType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.enterDistributionType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitSimpleType(
            CountlangParser.SimpleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.exitSimpleType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.exitDistributionType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.enterArrayType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.exitArrayType(antlrCtx, delegationCtx);
    }

    @Override
    public ExpressionNode getExpression() {
        expression.setCountlangType(CountlangType.arrayOf(typeHandler.getCountlangType()));
        return expression;
    }
}
