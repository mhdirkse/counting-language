package com.github.mhdirkse.countlang.types;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DistributionTest {
    private static final String EMPTY = "empty";
    private static final int FIRST_ITEM = 3;
    private static final int ITEM_LENGTH_2 = 52;
    private static final int COUNT_LENGTH_2 = 23;

    @Test
    public void whenDistributionEmptyThenFormattedAsEmpty() {
        Distribution d = (new Distribution.Builder()).build();
        Assert.assertEquals(EMPTY, d.format());
    }

    @Test
    public void whenDistributionHasValueThenFormattedWithValueAndTotal() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(FIRST_ITEM);
        List<String> expected = Arrays.asList(
                "    3  1",
                "--------",
                "total  1");
        Assert.assertEquals(String.join("\n", expected), b.build().format());
    }

    @Test
    public void whenDifferentFieldWithsThenAlignedRight() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(FIRST_ITEM);
        b.add(ITEM_LENGTH_2, COUNT_LENGTH_2);
        List<String> expected = Arrays.asList(
                "    3   1",
                "   52  23",
                "---------",
                "total  24");
        Assert.assertEquals(String.join("\n",  expected), b.build().format());
    }

    @Test
    public void whenDistributionHasValueThenCountAndTotalStored() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(FIRST_ITEM);
        Assert.assertEquals(1, b.build().getCountOf(FIRST_ITEM));
        Assert.assertEquals(0, b.build().getCountOf(FIRST_ITEM+1));
        Assert.assertEquals(1, b.build().getTotal());
    }

    @Test
    public void whenSameAddedMultipleTimesThenCountsAdded() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(FIRST_ITEM, 2);
        b.add(FIRST_ITEM, 5);
        Assert.assertEquals(7, b.build().getCountOf(FIRST_ITEM));
        Assert.assertEquals(7, b.build().getTotal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCountIsNegativeThenError() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(FIRST_ITEM, -1);
    }

    @Test
    public void whenItemAddedWithCountZeroThenUnchanged() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(FIRST_ITEM, 0);
        Assert.assertEquals(EMPTY, b.build().format());
    }

    @Test
    public void whenItemAddedWithCountZeroThenUnchanged_2() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(ITEM_LENGTH_2);
        b.add(FIRST_ITEM, 0);
        List<String> expected = Arrays.asList(
                "   52  1",
                "--------",
                "total  1");
        Assert.assertEquals(String.join("\n", expected), b.build().format());
    }

    @Test
    public void whenUnknownScoredThenUnknownShown() {
        Distribution.Builder b = new Distribution.Builder();
        b.addUnknown(1);
        List<String> expected = Arrays.asList(
                "unknown  1",
                "----------",
                "  total  1");
        Assert.assertEquals(String.join("\n", expected), b.build().format());
    }

    @Test
    public void whenUnknownScoredMultipleTimesThenAdded() {
        Distribution.Builder b = new Distribution.Builder();
        b.addUnknown(1);
        b.addUnknown(2);
        List<String> expected = Arrays.asList(
                "unknown  3",
                "----------",
                "  total  3");
        Assert.assertEquals(String.join("\n", expected), b.build().format());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUnknownCountNegativeThenError() {
        Distribution.Builder b = new Distribution.Builder();
        b.addUnknown(-1);
    }
}
