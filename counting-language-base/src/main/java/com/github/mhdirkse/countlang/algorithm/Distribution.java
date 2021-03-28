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

package com.github.mhdirkse.countlang.algorithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Distribution implements Comparable<Distribution> {
    public static class Builder {
        private Map<Object, Integer> items = new HashMap<>();
        private int total = 0;

        public void add(Object item) {
            add(item, 1);
        }

        public void add(Object item, int count) {
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
            for(Object item: items.keySet()) {
                items.put(item, factor * items.get(item));
            }
            total *= factor;
        }

        public int getTotal() {
            return total;
        }

        public Set<Object> getItems() {
            return new HashSet<>(items.keySet());
        }

        public Distribution build() {
            return new Distribution(this);
        }
    }

    private final Map<Object, Integer> items;
    private final int total;
    private final int unknown;

    private Distribution(final Builder builder) {
        Map<Object, Integer> newItems = new TreeMap<>();
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
        for(Object item: items.keySet()) {
            b.add(item, items.get(item));
        }
        return b.build();
    }

    public int getCountOf(Object value) {
        return items.getOrDefault(value, 0);
    }

    public int getTotal() {
        return total;
    }

    public int getCountUnknown() {
        return unknown;
    }

    public Iterator<Object> getItemIterator() {
        return items.keySet().iterator();
    }

    public Distribution normalize() {
        if(total == 0) {
            return new Distribution.Builder().build();
        }
        List<BigInteger> gcdInput = Stream.concat(items.values().stream(), Arrays.asList(unknown).stream())
                .filter(v -> v != 0)
                .map(b -> BigInteger.valueOf(b))
                .collect(Collectors.toList());
        int gcd = getGcd(gcdInput);
        Distribution.Builder b = new Distribution.Builder();
        for(Object item: items.keySet()) {
            b.add(item, items.get(item) / gcd);
        }
        b.addUnknown(unknown / gcd);
        return b.build();
    }

    private static int getGcd(List<BigInteger> values) {
        Optional<BigInteger> gcd = values.stream().collect(Collectors.reducing((b1, b2) -> b1.gcd(b2)));
        return gcd.get().intValue();
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
        for(Object item: items.keySet()) {
            String strItem = item.toString();
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

    /**
     * Distributions are sorted as follows. Every distribution being sorted is transformed
     * into a list and then lexicographical sorting is applied. Example: (distribution 2 of 2, 3)
     * is sorted as if it were (distribution 2, 2, 3). And when (distribution 1, 2) and
     * (distribution 2, 2, 3) are compared, then the former goes first because 1 &lt; 2.
     */
    @Override
    public int compareTo(Distribution other) {
        List<Comparable<Object>> itemsOfThis = writeOut();
        List<Comparable<Object>> itemsOfOther = other.writeOut();
        int numToCompare = Math.min(itemsOfThis.size(), itemsOfOther.size());
        int result = 0;
        for(int i = 0; i < numToCompare; i++) {
            result = itemsOfThis.get(i).compareTo(itemsOfOther.get(i));
            if(result != 0) {
                return result;
            }
        }
        result = Integer.compare(itemsOfThis.size(), itemsOfOther.size());
        if(result != 0) {
            return result;
        }
        result = Integer.compare(getTotal(), other.getTotal());
        return result;
    }

    private List<Comparable<Object>> writeOut() {
        final List<Comparable<Object>> result = new ArrayList<>(getTotal() - getCountUnknown());
        for(Object item: items.keySet()) {
            @SuppressWarnings("unchecked")
            Comparable<Object> c = (Comparable<Object>) item;
            IntStream.range(0, items.get(item)).forEach(i -> result.add(c));
        }
        return result;
    }

    @Override
    public String toString() {        
        List<Comparable<Object>> items = writeOut();
        String result = Stream.concat(items.stream().map(Object::toString), IntStream.range(0, unknown).mapToObj(i -> "unknown"))
                .collect(Collectors.joining(", "));
        return "(" + result + ")";
    }
}
