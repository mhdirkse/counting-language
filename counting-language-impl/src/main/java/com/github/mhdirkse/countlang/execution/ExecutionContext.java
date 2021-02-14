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

package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.algorithm.StackFrameAccess;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;

interface ExecutionContext extends StepperCallback {
    Object readSymbol(String symbol, AstNode node);
    void writeSymbol(String symbol, Object value, AstNode node);
    void pushVariableFrame(StackFrameAccess access);
    void popVariableFrame();
    boolean hasFunction(String name);
    void defineFunction(FunctionDefinitionStatementBase functionDefinitionStatement);
    FunctionDefinitionStatementBase getFunction(String functionName);
    void output(String text);
}
