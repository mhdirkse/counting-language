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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.utils.Stack;

public class SymbolFrameStack {
    private final Stack<SymbolFrame> frameStack;

    public SymbolFrameStack() {
        frameStack = new Stack<>();
    }

    public SymbolFrameStack(SymbolFrameStack orig) {
        final Stack<SymbolFrame> copied = new Stack<>();
        orig.frameStack.forEach(f -> copied.push(new SymbolFrame(f)));
        frameStack = copied;
    }

    public final void pushFrame(StackFrameAccess stackFrameAccess) {
        frameStack.push(create(stackFrameAccess));
    }

    public final void popFrame() {
        frameStack.pop();
    }

    public final Object read(String name, int line, int column) {
        return findFrame(name).read(name, line, column);
    }

    public final void write(String name, Object value, int line, int column) {
        findFrame(name).write(name, value, line, column);
    }

    SymbolFrame create(StackFrameAccess access) {
        return new SymbolFrame(access);
    }

    final SymbolFrame findFrame(String name) {
        List<SymbolFrame> accessibleFrames = getAccessibleFrames();
        if(accessibleFrames.isEmpty()) {
            throw new IllegalStateException("No symbol frames to search for symbol: " + name);
        }
        for(SymbolFrame s: accessibleFrames) {
            if(s.has(name)) {
                return s;
            }
        }
        return accessibleFrames.get(0);
    }

    private List<SymbolFrame> getAccessibleFrames() {
        List<SymbolFrame> result = new ArrayList<>();
        Iterator<SymbolFrame> it = frameStack.topToBottomIterator();
        while(it.hasNext()) {
            SymbolFrame current = it.next();
            result.add(current);
            if(current.getAccess() == StackFrameAccess.HIDE_PARENT) {
                break;
            }
        }
        return result;
    }
}
