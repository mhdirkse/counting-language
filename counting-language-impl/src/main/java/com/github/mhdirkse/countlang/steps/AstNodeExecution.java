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

interface AstNodeExecution {
    AstNode getAstNode();
    AstNodeExecutionState getState();

    default ExecutionPointNode getExecutionPointNode() {
        return new ExecutionPointNode(getAstNode(), getState());
    }

    AstNode step(ExecutionContext context);

    default boolean isAcceptingChildResults() {
        return false;
    }

    default void acceptChildResult(Object value, ExecutionContext context) {
        throw new IllegalStateException("Programming error. Did not expect child result");
    }

    default AstNodeExecution fork() {
        throw new IllegalStateException("Fork only happens in sample statements or experiment definitions; this element should not be in the call stack then");
    }
}
