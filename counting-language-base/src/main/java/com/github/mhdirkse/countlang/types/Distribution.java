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

package com.github.mhdirkse.countlang.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public final class Distribution {
    public static class Builder {
        private Map<Integer, Integer> items = new HashMap<>();
        private int total = 0;

        public void add(int item) {
            add(item, 1);
        }

        public void add(int item, int count) {
            if(count < 0) {
                throw new IllegalArgumentException("Cannot reduce the count of an item");
            } else if(count == 0) {
                return;
            }
            items.merge(item, count, (c1, c2) -> c1 + c2);
            total += count;
        }

        public void addUnknown(int countOfUnknown) {
            if(countOfUnknown < 0) {
                throw new IllegalArgumentException("Count of unknown cannot be negative");
            }
            total += countOfUnknown;
        }

        public void refine(int factor) {
            if(factor <= 0) {
                throw new IllegalArgumentException(
                        "Refining with factor zero or a negative factor is not allowed, tried: " + Integer.toString(factor));
            }
            for(int item: items.keySet()) {
                items.put(item, factor * items.get(item));
            }
            total *= factor;
        }

        public int getTotal() {
            return total;
        }

        public Distribution build() {
            return new Distribution(this);
        }
    }

    private final Map<Integer, Integer> items;
    private final int total;
    private final int unknown;

    private Distribution(final Builder builder) {
        Map<Integer, Integer> newItems = new HashMap<>();
        newItems.putAll(builder.items);
        this.items = newItems;
        this.total = builder.total;
        unknown = total - this.items.values().stream().reduce(0, (i1, i2) -> i1 + i2);
        if(unknown < 0) {
            throw new IllegalArgumentException("Count of unknown should be non-negative");
        }
    }

    public Distribution getDistributionOfKnown() {
        Builder b = new Builder();
        for(Integer item: items.keySet()) {
            b.add(item, items.get(item));
        }
        return b.build();
    }

    public int getCountOf(int value) {
        return items.getOrDefault(value, 0);
    }

    public int getTotal() {
        return total;
    }

    public int getCountUnknown() {
        return unknown;
    }

    public Iterator<Integer> getItemIterator() {
        return items.keySet().iterator();
    }

    public String format() {
        if(total == 0) {
            return "empty";
        }
        List<List<String>> table = createTable();
        final List<Integer> columnWidths = getTableColumnWidths(table);
        List<String> result = table.stream()
                .map(row -> formatTableRow(row, columnWidths))
                .collect(Collectors.toList());
        result.add(table.size()-1, getSeparator(columnWidths));
        return String.join("\n", result);
    }

    private List<List<String>> createTable() {
        List<List<String>> table = new ArrayList<>();
        List<Integer> sortedItems = new ArrayList<>(items.keySet());
        sortedItems.sort((i1, i2) -> i1.compareTo(i2));
        for(int item: sortedItems) {
            String strItem = Integer.toString(item);
            String strCount = Integer.toString(items.get(item));
            table.add(Arrays.asList(strItem, strCount));
        }
        if(unknown >= 1) {
            table.add(Arrays.asList("unknown", Integer.toString(unknown)));
        }
        table.add(Arrays.<String>asList("total", Integer.toString(total)));
        return table;
    }

    private static List<Integer> getTableColumnWidths(List<List<String>> table) {
        List<Integer> columnWidths = new ArrayList<>();
        for(int column = 0; column < 2; column++) {
            final int theColumn = column;
            int width = table.stream()
                    .map(row -> row.get(theColumn).length())
                    .max((i1, i2) -> i1.compareTo(i2)).get();
            columnWidths.add(width);
        }
        return columnWidths;
    }

    private static String getSeparator(final List<Integer> columnWidths) {
        int totalWidth = columnWidths.stream().reduce(0, (i1, i2) -> i1 + i2) + 2;
        String separator = StringUtils.repeat('-', totalWidth);
        return separator;
    }

    private static String formatTableRow(List<String> row, List<Integer> columnWidths) {
        String value = StringUtils.leftPad(row.get(0), columnWidths.get(0), " ");
        String count = StringUtils.leftPad(row.get(1), columnWidths.get(1), " ");
        return value + "  " + count;
    }
}
