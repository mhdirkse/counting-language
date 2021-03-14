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

package com.github.mhdirkse.countlang.lang.parsing;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class VarDeclsHandler extends AbstractCountlangListenerHandler {
    VarDeclsHandler() {
        super(false);
    }

    private List<FormalParameter> formalParameters = new ArrayList<>();

    List<FormalParameter> getFormalParameters() {
        return formalParameters;
    }

    @Override
    public boolean enterVarDecl(
            CountlangParser.VarDeclContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new VarDeclHandler(line, column));
        return true;
    }

    @Override
    public boolean exitVarDecl(
            CountlangParser.VarDeclContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(delegationCtx.isFirst()) {
            return false;
        } else {
            VarDeclHandler handler = ((VarDeclHandler) delegationCtx.getPreviousHandler());
            formalParameters.add(new FormalParameter(handler.getLine(), handler.getColumn(), handler.getId(), handler.getCountlangType()));
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }
}
