package com.github.mhdirkse.countlang.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.github.mhdirkse.utils.AbstractStatusCode;

import lombok.Getter;

public class SortingStatusReporter implements StatusReporter {
    private static class Item {
        private @Getter int line;
        private @Getter int column;
        private @Getter StatusCode statusCode;
        private List<String> others;
        
        Item(StatusCode statusCode, int line, int column, List<String> others) {
            this.line = line;
            this.column = column;
            this.statusCode = statusCode;
            this.others = others;
        }

        private String[] othersAsStringArray() {
            String[] result = new String[others.size()];
            for(int i = 0; i < others.size(); i++) {
                result[i] = others.get(i);
            }
            return result;
        }

        static Comparator<Item> comparator() {
            Function<Item, StatusCode> f = item -> item.getStatusCode();
            Comparator<Item> byLine = Comparator.comparingInt(item -> item.getLine());
            Comparator<Item> byColumn = Comparator.comparingInt(item -> item.getColumn());
            Comparator<Item> byStatusCode = Comparator.<Item, StatusCode>comparing(f);
            return byLine.thenComparing(byColumn).thenComparing(byStatusCode);
        }
    }

    private List<Item> items = new ArrayList<>();

    /**
     * Expects {@link StatusCode} instead of {@link AbstractStatusCode}
     */
    @Override
    public void report(AbstractStatusCode statusCode, int line, int column, String... others) {
        items.add(new Item((StatusCode) statusCode, line, column, Arrays.asList(others)));
    }

    @Override
    public boolean hasErrors() {
        return ! items.isEmpty();
    }

    public void reportTo(StatusReporter reporter) {
        Collections.sort(items, Item.comparator());
        items.forEach(i -> reporter.report(i.getStatusCode(), i.getLine(), i.getColumn(), i.othersAsStringArray()));
    }
}
