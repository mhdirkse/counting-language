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

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.Distribution;

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

    @Test
    public void testRefineWithoutUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(3);
        b.add(3);
        b.refine(2);
        Distribution d = b.build();
        Assert.assertEquals(4, d.getCountOf(3));
        Assert.assertEquals(4, d.getTotal());
        Assert.assertEquals(0, d.getCountUnknown());
    }

    @Test
    public void testRefineWithUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.addUnknown(2);
        b.refine(3);
        Distribution d = b.build();
        Assert.assertEquals(6, d.getCountUnknown());
        Assert.assertEquals(6, d.getTotal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenRefineWithNegativeFactorThenError() {
        Distribution.Builder b = new Distribution.Builder();
        b.refine(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenRefineWithFactorZeroThenError() {
        Distribution.Builder b = new Distribution.Builder();
        b.refine(0);
    }

    @Test
    public void testGetDistributionOfKnown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(2, 1);
        b.add(3, 4);
        b.addUnknown(5);
        Distribution result = b.build().getDistributionOfKnown();
        Assert.assertEquals(5, result.getTotal());
        Assert.assertEquals(0, result.getCountUnknown());
        Assert.assertEquals(1, result.getCountOf(2));
        Assert.assertEquals(4, result.getCountOf(3));
    }
}
