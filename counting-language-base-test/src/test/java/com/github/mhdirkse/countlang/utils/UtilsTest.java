package com.github.mhdirkse.countlang.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class UtilsTest {
    private static final List<List<Object>> ITEMS = Arrays.asList(
            new ArrayList<>(),
            Arrays.asList(1),
            Arrays.asList(1, 1),
            Arrays.asList(1, 2),
            Arrays.asList(2));

    @Test
    public void emptySmallerThenAny() {
        for(int i = 1; i < ITEMS.size(); ++i) {
            String msg = String.format("Item %d compareTo item %d should be %d", 0, i, -1);
            assertEquals(msg, -1, compare(0, i));
            msg = String.format("Item %d compareTo item %d should be %d", i, 0, 1);
            assertEquals(msg, 1, compare(i, 0));
        }
    }

    private int compare(int firstIdx, int secondIdx) {
        List<Object> first = ITEMS.get(firstIdx);
        List<Object> second = ITEMS.get(secondIdx);
        int result = ListComparator.getInstance().compare(first, second);
        if(result < 0) {
            return -1;
        }
        if(result > 0) {
            return 1;
        }
        return 0;
    }

    @Test
    public void firstDifferentItemMoreImportantThanSize() {
        assertEquals(-1, compare(2, 4));
        assertEquals(1, compare(4, 2));
    }

    @Test
    public void sizeDecidesWhenComparableItemsEqual() {
        assertEquals(-1, compare(1, 2));
        assertEquals(1, compare(2, 1));
    }

    @Test
    public void canHaveEquality() {
        assertEquals(0, compare(0, 0));
        assertEquals(0, compare(1, 1));
    }
}
