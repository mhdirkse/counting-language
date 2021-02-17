package com.github.mhdirkse.countlang.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.github.mhdirkse.countlang.utils.Stack;

public class CountlangStack<T extends CountlangStackItem> {
    private final Stack<T> stack;

    public CountlangStack() {
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

    public T findFrame(String name) {
        List<T> accessibleFrames = getAccessibleFrames();
        if(accessibleFrames.isEmpty()) {
            throw new IllegalStateException("No symbol frames to search for symbol: " + name);
        }
        for(T s: accessibleFrames) {
            if(s.has(name)) {
                return s;
            }
        }
        return accessibleFrames.get(0);
    }

    private List<T> getAccessibleFrames() {
        List<T> result = new ArrayList<>();
        Iterator<T> it = stack.topToBottomIterator();
        while(it.hasNext()) {
            T current = it.next();
            result.add(current);
            if(current.getAccess() == StackFrameAccess.HIDE_PARENT) {
                break;
            }
        }
        return result;
    }
}
