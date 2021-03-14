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

package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.ScopeStack;
import com.github.mhdirkse.countlang.ast.CountlangType;

class MemoryImpl implements Memory {
    private ScopeStack<AnalysisScope> analysisScopes = new ScopeStack<AnalysisScope>();
    private List<VariableErrorEvent> variableErrorEvents = new ArrayList<>();

    @Override
    public List<VariableErrorEvent> getVariableErrorEvents() {
        return Collections.unmodifiableList(variableErrorEvents);
    }

    @Override
    public void pushScope(AnalysisScope analysisScope) {
        analysisScopes.push(analysisScope);
    }

    @Override
    public AnalysisScope popScope() {
        return analysisScopes.pop();
    }

    @Override
    public boolean isAtRootScope() {
        return analysisScopes.size() == 1;
    }

    /**
     * Enables default implementations of interface {@link BlockListener}.
     */
    @Override
    public Iterator<AnalysisScope> getChildren() {
        return analysisScopes.topToBottomIterator();
    }

    @Override
    public CountlangType read(String name, int line, int column, CodeBlock codeBlock) {
        AnalysisScope analysisScope = analysisScopes.findScope(name);
        if(analysisScope.has(name)) {
            return analysisScope.read(name, line, column, codeBlock);
        } else {
            variableErrorEvents.add(new VariableErrorEvent(VariableErrorEvent.Kind.DOES_NOT_EXIST, name, line, column));
            return CountlangType.unknown();
        }
    }

    @Override
    public void write(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        VariableErrorEvent optionalTypeMismatch = analysisScopes.findScope(name).write(name, line, column, countlangType, codeBlock);
        if(optionalTypeMismatch != null) {
            variableErrorEvents.add(optionalTypeMismatch);
        }
    }

    @Override
    public void addParameter(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        AnalysisScope analysisScope = analysisScopes.findScope(name);
        if(analysisScope.has(name)) {
            variableErrorEvents.add(new VariableErrorEvent(VariableErrorEvent.Kind.DUPLICATE_PARAMETER, name, line, column));
        } else {
            analysisScope.addParameter(name, line, column, countlangType, codeBlock);
        }
    }
}
