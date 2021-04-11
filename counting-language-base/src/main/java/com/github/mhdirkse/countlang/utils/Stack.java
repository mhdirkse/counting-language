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

package com.github.mhdirkse.countlang.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public <S> Stream<S> applyToAll(Function<T, S> fun) {
        return stack.stream().map(fun);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }
}
