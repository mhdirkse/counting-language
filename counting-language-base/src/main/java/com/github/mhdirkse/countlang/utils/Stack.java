package com.github.mhdirkse.countlang.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Stack<T> {
    private final Deque<T> stack = new ArrayDeque<>();
    
    public void push(T item) {
        stack.addLast(item);
    }

    public T pop() {
        return stack.removeLast();
    }

    public T peek() {
        return stack.peekLast();
    }

    public void pushAll(List<T> items) {
        for(T item : items) {
            push(item);
        }
    }

    public List<T> repeatedPop(int n) {
        List<T> result = new ArrayList<>(n);
        IntStream
            .rangeClosed(1, n)
            .forEach(i -> result.add(pop()));
        Collections.reverse(result);
        return result;
    }

    public Iterator<T> topToBottomIterator() {
        return stack.descendingIterator();
    }

    public void forEach(Consumer<T> consumer) {
        stack.forEach(consumer);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }
}
