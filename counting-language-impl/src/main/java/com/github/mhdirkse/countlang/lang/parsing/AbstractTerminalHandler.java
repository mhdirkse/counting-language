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

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;

abstract class AbstractTerminalHandler
extends AbstractCountlangListenerHandler
implements TerminalFilterCallback
{
    static int ANY_TYPE = -1;

    AbstractTerminalHandler() {
        super(false);
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode terminalNode,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if (delegationCtx.isFirst() && isRequiredType(terminalNode)) {
            setText(terminalNode.getText());
            return true;
        } else {
            return false;
        }
    }

    private boolean isRequiredType(final TerminalNode terminalNode) {
        int actualType = terminalNode.getSymbol().getType();
        int requiredType = getRequiredType();
        return (requiredType == ANY_TYPE) || (actualType == requiredType);
    }
}
