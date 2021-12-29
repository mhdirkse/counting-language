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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.mhdirkse.countlang.algorithm.Scope;
import com.github.mhdirkse.countlang.algorithm.ScopeAccess;
import com.github.mhdirkse.countlang.type.CountlangType;

class AnalysisScope implements BlockListener, Scope {
    private final ScopeAccess access;

    private Map<String, Variable> variables = new HashMap<>();

    AnalysisScope(ScopeAccess access) {
        this.access = access;
    }

    @Override
    public ScopeAccess getAccess() {
        return access;
    }

    /**
     * Enables the default implementations of interface {@link BlockListener}.
     */
    @Override
    public Iterator<Variable> getChildren() {
        return variables.values().iterator();
    }

    @Override
    public boolean has(String name) {
        return variables.containsKey(name);
    }

    CountlangType read(String name, int line, int column, CodeBlock codeBlock) {
        return variables.get(name).read(codeBlock);
    }

    VariableErrorEvent write(String name, int line, int column, CountlangType countlangType, VariableWriteKind kind, CodeBlock codeBlock) {
        if(variables.containsKey(name)) {
            return variables.get(name).write(line, column, countlangType, kind, codeBlock);
        } else {
            Variable v = new Variable(name, line, column, countlangType, kind, codeBlock);
            variables.put(v.getName(), v);
            return null;
        }
    }

    void addParameter(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        Variable v = new Variable(name, line, column, countlangType, VariableWriteKind.PARAMETER, codeBlock);
        variables.put(v.getName(), v);
    }
}
