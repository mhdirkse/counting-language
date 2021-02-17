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
