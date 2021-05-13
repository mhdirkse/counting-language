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
    private static final BigInteger MAX_INT_VALUE = new BigInteger(Integer.valueOf(Integer.MAX_VALUE).toString());

    public static class Builder {
        private Map<Object, BigInteger> items = new HashMap<>();
        private BigInteger total = BigInteger.ZERO;

        public void add(int item) {
            BigInteger bigItem = new BigInteger(Integer.valueOf(item).toString());
            add(bigItem);
        }

        public void add(int item, int count) {
            BigInteger bigItem = new BigInteger(Integer.valueOf(item).toString());
            BigInteger bigCount = new BigInteger(Integer.valueOf(count).toString());
            add(bigItem, bigCount);
        }

        public void add(Object item) {
            add(item, BigInteger.ONE);
        }

        public void add(Object item, int count) {
            BigInteger bigCount = new BigInteger(Integer.valueOf(count).toString());
            add(item, bigCount);
        }

        public void add(Object item, BigInteger count) {
            int comparedToZero = count.compareTo(BigInteger.ZERO);
            if(comparedToZero < 0) {
                throw new IllegalArgumentException("Cannot reduce the count of an item");
            } else if(comparedToZero == 0) {
                return;
            }
            item = itemIntToBigint(item);
            items.merge(item, count, (c1, c2) -> c1.add(c2));
            total = total.add(count);
        }

        private Object itemIntToBigint(Object original) {
            if(original instanceof Integer) {
                return new BigInteger(((Integer) original).toString());
            } else {
                return original;
            }
        }

        public void addUnknown(int countOfUnknown) {
            BigInteger bigCountOfUnknown = new BigInteger(Integer.valueOf(countOfUnknown).toString());
            addUnknown(bigCountOfUnknown);
        }

        public void addUnknown(BigInteger countOfUnknown) {
            int compareToZero = countOfUnknown.compareTo(BigInteger.ZERO);
            if(compareToZero < 0) {
                throw new IllegalArgumentException("Count of unknown cannot be negative");
            }
            total = total.add(countOfUnknown);
        }

        public void refine(int factor) {
            BigInteger bigFactor = new BigInteger(Integer.valueOf(factor).toString());
            refine(bigFactor);
        }

        public void refine(BigInteger factor) {
            int compareToZero = factor.compareTo(BigInteger.ZERO);
            if(compareToZero <= 0) {
                throw new IllegalArgumentException(
                        "Refining with factor zero or a negative factor is not allowed, tried: " + factor.toString());
            }
            for(Object item: items.keySet()) {
                items.put(item, factor.multiply(items.get(item)));
            }
            total = total.multiply(factor);
        }

        public BigInteger getTotal() {
            return total;
        }

        public Set<Object> getItems() {
            return new HashSet<>(items.keySet());
        }

        public Distribution build() {
            return new Distribution(this);
        }
    }

    private final Map<Object, BigInteger> items;
    private final BigInteger total;
    private final BigInteger unknown;

    private Distribution(final Builder builder) {
        Map<Object, BigInteger> newItems = new TreeMap<>();
        newItems.putAll(builder.items);
        this.items = newItems;
        this.total = builder.total;
        unknown = total.subtract(this.items.values().stream().reduce(BigInteger.ZERO, (i1, i2) -> i1.add(i2)));
        if(unknown.compareTo(BigInteger.ZERO) < 0) {
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

    public BigInteger getCountOf(Object value) {
        return items.getOrDefault(value, BigInteger.ZERO);
    }

    public BigInteger getTotal() {
        return total;
    }

    public BigInteger getCountUnknown() {
        return unknown;
    }

    public BigInteger getCountOf(ProbabilityTreeValue value) {
        if(value.isUnknown()) {
            return getCountUnknown();
        } else {
            return getCountOf(value.getValue());
        }
    }

    public Iterator<Object> getItemIterator() {
        return items.keySet().iterator();
    }

    public Distribution normalize() {
        if(total.equals(BigInteger.ZERO)) {
            return new Distribution.Builder().build();
        }
        List<BigInteger> gcdInput = Stream.concat(items.values().stream(), Arrays.asList(unknown).stream())
                .filter(v -> ! v.equals(BigInteger.ZERO))
                .collect(Collectors.toList());
        BigInteger gcd = getGcd(gcdInput);
        Distribution.Builder b = new Distribution.Builder();
        for(Object item: items.keySet()) {
            b.add(item, items.get(item).divide(gcd));
        }
        b.addUnknown(unknown.divide(gcd));
        return b.build();
    }

    private static BigInteger getGcd(List<BigInteger> values) {
        Optional<BigInteger> gcd = values.stream().collect(Collectors.reducing((b1, b2) -> b1.gcd(b2)));
        return gcd.get();
    }

    public String format() {
        if(total.equals(BigInteger.ZERO)) {
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
            String strCount = items.get(item).toString();
            table.add(Arrays.asList(strItem, strCount));
        }
        if(unknown.compareTo(BigInteger.ZERO) > 0) {
            table.add(Arrays.asList("unknown", unknown.toString()));
        }
        table.add(Arrays.<String>asList("total", total.toString()));
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
        result = getTotal().compareTo(other.getTotal());
        return result;
    }

    private List<Comparable<Object>> writeOut() {
        if(getTotal().compareTo(MAX_INT_VALUE) > 0) {
            throw new IllegalStateException("Cannot compare distributions because they are too big");
        }
        BigInteger numItemsInResultRaw = getTotal().subtract(getCountUnknown());
        int numItemsInResult = numItemsInResultRaw.intValue();
        final List<Comparable<Object>> result = new ArrayList<>(numItemsInResult);
        for(Object item: items.keySet()) {
            @SuppressWarnings("unchecked")
            Comparable<Object> c = (Comparable<Object>) item;
            IntStream.range(0, items.get(item).intValue()).forEach(i -> result.add(c));
        }
        return result;
    }

    @Override
    public String toString() {        
        List<Comparable<Object>> items = writeOut();
        String result = Stream.concat(items.stream().map(Object::toString), IntStream.range(0, unknown.intValue()).mapToObj(i -> "unknown"))
                .collect(Collectors.joining(", "));
        return "(" + result + ")";
    }
}
