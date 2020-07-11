package com.github.mhdirkse.countlang.execution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.IntStream;

public class CountlangStack<T> {
    private final Deque<T> stack = new ArrayDeque<>();
    
    public <S extends T> void push(S item) {
        stack.addLast(item);
    }

    public T pop() {
        return stack.removeLast();
    }

    public List<T> repeatedPop(int n) {
        List<T> result = new ArrayList<>(n);
        IntStream
            .rangeClosed(1, n)
            .forEach(i -> result.add(pop()));
        return result;
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
