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

import java.util.function.Consumer;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.lang.CountlangParser;
import com.github.mhdirkse.countlang.type.CountlangType;

public class TypeIdHandler extends AbstractTypeHandler {
    private CountlangType countlangType = CountlangType.unknown();
    
    TypeIdHandler(int line, int column) {
        super(line, column);
    }

    @Override
    CountlangType getCountlangType() {
        return countlangType;
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
        return handleTypeExit(delegationCtx, subType -> countlangType = subType);
    }

    private boolean handleTypeExit(final HandlerStackContext<CountlangListenerHandler> delegationCtx, Consumer<CountlangType> subTypeHandler) {
        if(delegationCtx.isFirst()) {
            return false;
        } else {
            AbstractTypeHandler typeHandler = ((AbstractTypeHandler) delegationCtx.getPreviousHandler());;
            subTypeHandler.accept(typeHandler.getCountlangType());
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }

    @Override
    public boolean enterDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new TypeIdHandler(line, column));
        return true;
    }

    @Override
    public boolean exitDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx, subType -> countlangType = CountlangType.distributionOf(subType));
    }

    @Override
    public boolean enterArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new TypeIdHandler(line, column));
        return true;
    }

    @Override
    public boolean exitArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx, subType -> countlangType = CountlangType.arrayOf(subType));
    }
}
