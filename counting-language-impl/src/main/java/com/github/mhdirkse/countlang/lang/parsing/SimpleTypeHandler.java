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

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.AtomicTypeNode;
import com.github.mhdirkse.countlang.ast.TypeNode;
import com.github.mhdirkse.countlang.lang.CountlangLexer;
import com.github.mhdirkse.countlang.type.CountlangType;

class SimpleTypeHandler extends AbstractCountlangListenerHandler implements TypeIdSource {
    private final int line;
    private final int column;
    private AtomicTypeNode typeNode;

    SimpleTypeHandler(int line, int column) {
        super(false);
        this.line = line;
        this.column = column;
    }

    @Override
    public TypeNode getTypeNode() {
        return typeNode;
    }

    @Override
    public boolean visitTerminal(
            TerminalNode antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int antlrType = antlrCtx.getSymbol().getType();
        if(antlrType == CountlangLexer.BOOLTYPE) {
            typeNode = new AtomicTypeNode(line, column, CountlangType.bool());
        } else if(antlrType == CountlangLexer.INTTYPE) {
            typeNode = new AtomicTypeNode(line, column, CountlangType.integer());
        } else if(antlrType == CountlangLexer.FRACTYPE) {
            typeNode = new AtomicTypeNode(line, column, CountlangType.fraction());
        } else {
            throw new IllegalArgumentException("Unknown type");
        }
        return true;
    }
}
