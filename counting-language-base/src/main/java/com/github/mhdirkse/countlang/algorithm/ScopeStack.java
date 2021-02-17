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

package com.github.mhdirkse.countlang.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.github.mhdirkse.countlang.utils.Stack;

public class ScopeStack<T extends Scope> {
    private final Stack<T> stack;

    public ScopeStack() {
        stack = new Stack<T>();
    }

    public void push(T item) {
        stack.push(item);
    }

    public T pop() {
        return stack.pop();
    }

    public void forEach(Consumer<T> consumer) {
        stack.forEach(consumer);
    }

    public int size() {
        return stack.size();
    }

    public Iterator<T> topToBottomIterator() {
        return stack.topToBottomIterator();
    }

    public T findScope(String symbol) {
        List<T> accessibleScopes = getAccessibleScopes();
        if(accessibleScopes.isEmpty()) {
            throw new IllegalStateException("No symbol frames to search for symbol: " + symbol);
        }
        for(T s: accessibleScopes) {
            if(s.has(symbol)) {
                return s;
            }
        }
        return accessibleScopes.get(0);
    }

    private List<T> getAccessibleScopes() {
        List<T> result = new ArrayList<>();
        Iterator<T> it = stack.topToBottomIterator();
        while(it.hasNext()) {
            T current = it.next();
            result.add(current);
            if(current.getAccess() == ScopeAccess.HIDE_PARENT) {
                break;
            }
        }
        return result;
    }
}
