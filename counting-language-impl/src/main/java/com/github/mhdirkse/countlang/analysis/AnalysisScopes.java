package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;
import com.github.mhdirkse.countlang.utils.Stack;

class AnalysisScopes {
    private Stack<AnalysisScope> frames = new Stack<>();

    void push(AnalysisScope scope) {
        frames.push(scope);
    }

    void pop() {
        frames.pop();
    }

    CountlangType read(String name, int line, int column) throws ReadUndefinedSymbolException {
        AnalysisScope scope = null;
        scope = findScope(name);
        if(! scope.has(name)) {
            throw new ReadUndefinedSymbolException();
        }
        return scope.read(name, line, column);
    }

    /**
     * Find the scope to read a variable from or write a variable to.
     * @throws IllegalStateException when there are no scopes to search. This should not happen.
     */
    final AnalysisScope findScope(String name) {
        List<AnalysisScope> accessibleFrames = getAccessibleFrames();
        if(accessibleFrames.isEmpty()) {
            throw new IllegalStateException("No scopes available to search");
        }
        for(AnalysisScope s: accessibleFrames) {
            if(s.has(name)) {
                return s;
            }
        }
        return accessibleFrames.get(0);
    }

    private List<AnalysisScope> getAccessibleFrames() {
        List<AnalysisScope> result = new ArrayList<>();
        Iterator<AnalysisScope> it = frames.topToBottomIterator();
        while(it.hasNext()) {
            AnalysisScope current = it.next();
            result.add(current);
            if(current.getAccess() == StackFrameAccess.HIDE_PARENT) {
                break;
            }
        }
        return result;
    }

    void write(String name, CountlangType countlangType, int line, int column) {
        findScope(name).write(name, countlangType, line, column);
    }

    void define(String name, CountlangType countlangType, int line, int column) {
        if(frames.isEmpty()) {
            throw new IllegalStateException(String.format("No scope available to define symbol [%s]", name));
        }
        frames.peek().define(name, countlangType, line, column);
    }

    void onControlEvent(ControlEvent ev) {
        frames.forEach(scope -> scope.onControlEvent(ev));
    }
}
