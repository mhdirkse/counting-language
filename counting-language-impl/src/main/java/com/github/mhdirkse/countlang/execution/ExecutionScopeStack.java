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

import com.github.mhdirkse.countlang.algorithm.ScopeStack;
import com.github.mhdirkse.countlang.algorithm.ScopeAccess;

public class ExecutionScopeStack {
    private final ScopeStack<ExecutionScope> scopeStack;

    public ExecutionScopeStack() {
        scopeStack = new ScopeStack<>();
    }

    public ExecutionScopeStack(ExecutionScopeStack orig) {
        final ScopeStack<ExecutionScope> copied = new ScopeStack<>();
        orig.scopeStack.forEach(f -> copied.push(new ExecutionScope(f)));
        scopeStack = copied;
    }

    public final void push(ScopeAccess scopeAccess) {
        scopeStack.push(create(scopeAccess));
    }

    public final void pop() {
        scopeStack.pop();
    }

    public final Object read(String name, int line, int column) {
        return scopeStack.findScope(name).read(name, line, column);
    }

    public final void write(String name, Object value, int line, int column) {
        scopeStack.findScope(name).write(name, value, line, column);
    }

    ExecutionScope create(ScopeAccess access) {
        return new ExecutionScope(access);
    }
}
