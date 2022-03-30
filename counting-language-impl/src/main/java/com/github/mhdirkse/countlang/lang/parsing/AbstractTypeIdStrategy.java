/*
 * Copyright Martijn Dirkse 2022
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
import com.github.mhdirkse.countlang.ast.TypeNode;
import com.github.mhdirkse.countlang.lang.CountlangParser;

public abstract class AbstractTypeIdStrategy extends AbstractCountlangListenerHandler implements TypeIdSource {
    AbstractTypeIdStrategy() {
        super(false);
    }

    @Override
    public boolean enterSimpleType(
            CountlangParser.SimpleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new SimpleTypeHandler(line, column));
        return true;
    }

    @Override
    public boolean exitSimpleType(
            CountlangParser.SimpleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx);
    }

    private boolean handleTypeExit(final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(delegationCtx.isFirst()) {
            return false;
        } else {
            TypeIdSource typeIdSource = ((TypeIdSource) delegationCtx.getPreviousHandler());
            TypeNode child = typeIdSource.getTypeNode();
            handleChild(child);
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }

    abstract void handleChild(TypeNode child);

    @Override
    public boolean enterDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new DistributionTypeIdHandler(line, column));
        return true;
    }

    @Override
    public boolean exitDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx);
    }

    @Override
    public boolean enterArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new ArrayTypeIdHandler(line, column));
        return true;
    }

    @Override
    public boolean exitArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx);
    }

    @Override
    public boolean enterTupleType(
            CountlangParser.TupleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new TupleTypeIdHandler(line, column));
        return true;        
    }

    @Override
    public boolean exitTupleType(
            CountlangParser.TupleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx);
    }    
}
