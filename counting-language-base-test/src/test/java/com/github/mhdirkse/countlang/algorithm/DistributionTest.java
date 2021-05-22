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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.TestFactory.DistributionBuilderInt2Bigint;

public class DistributionTest {
    private static final String EMPTY = "empty";
    private static final BigInteger FIRST_ITEM = new BigInteger("3");
    private static final int ITEM_LENGTH_2 = 52;
    private static final int COUNT_LENGTH_2 = 23;

    private TestFactory tf;

    @Before
    public void setUp() {
        tf = new TestFactory();
    }

    @Test
    public void whenDistributionEmptyThenFormattedAsEmpty() {
        Distribution d = (new Distribution.Builder()).build();
        Assert.assertEquals(EMPTY, d.format());
    }

    @Test
    public void whenDistributionHasValueThenFormattedWithValueAndTotal() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(FIRST_ITEM);
        List<String> expected = Arrays.asList(
                "    3  1",
                "--------",
                "total  1");
        Assert.assertEquals(String.join("\n", expected), b.build().format());
    }

    @Test
    public void whenDifferentFieldWithsThenAlignedRight() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
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
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(FIRST_ITEM);
        TestUtils.assertEqualsConvertingInt(1, b.build().getCountOf(FIRST_ITEM));
        TestUtils.assertEqualsConvertingInt(0, b.build().getCountOf(FIRST_ITEM.add(BigInteger.ONE)));
        TestUtils.assertEqualsConvertingInt(1, b.build().getTotal());
    }

    @Test
    public void whenSameAddedMultipleTimesThenCountsAdded() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(FIRST_ITEM, 2);
        b.add(FIRST_ITEM, 5);
        TestUtils.assertEqualsConvertingInt(7, b.build().getCountOf(FIRST_ITEM));
        TestUtils.assertEqualsConvertingInt(7, b.build().getTotal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCountIsNegativeThenError() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(FIRST_ITEM, -1);
    }

    @Test
    public void whenItemAddedWithCountZeroThenUnchanged() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(FIRST_ITEM, 0);
        Assert.assertEquals(EMPTY, b.build().format());
    }

    @Test
    public void whenItemAddedWithCountZeroThenUnchanged_2() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
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
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.addUnknown(1);
        List<String> expected = Arrays.asList(
                "unknown  1",
                "----------",
                "  total  1");
        Assert.assertEquals(String.join("\n", expected), b.build().format());
    }

    @Test
    public void whenUnknownScoredMultipleTimesThenAdded() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
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
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.addUnknown(-1);
    }

    @Test
    public void testRefineWithoutUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(3);
        b.add(3);
        b.refine(2);
        Distribution d = b.build();
        TestUtils.assertEqualsConvertingInt(4, d.getCountOf(new BigInteger("3")));
        TestUtils.assertEqualsConvertingInt(4, d.getTotal());
        TestUtils.assertEqualsConvertingInt(0, d.getCountUnknown());
    }

    @Test
    public void testRefineWithUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.addUnknown(2);
        b.refine(3);
        Distribution d = b.build();
        TestUtils.assertEqualsConvertingInt(6, d.getCountUnknown());
        TestUtils.assertEqualsConvertingInt(6, d.getTotal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenRefineWithNegativeFactorThenError() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.refine(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenRefineWithFactorZeroThenError() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.refine(0);
    }

    @Test
    public void testGetDistributionOfKnown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(2, 1);
        b.add(3, 4);
        b.addUnknown(5);
        Distribution result = b.build().getDistributionOfKnown();
        TestUtils.assertEqualsConvertingInt(5, result.getTotal());
        TestUtils.assertEqualsConvertingInt(0, result.getCountUnknown());
        TestUtils.assertEqualsConvertingInt(1, result.getCountOf(new BigInteger("2")));
        TestUtils.assertEqualsConvertingInt(4, result.getCountOf(new BigInteger("3")));
    }

    @Test
    public void testDistributionComparison() {
        TestFactory tf = new TestFactory();
        List<Distribution> toSort = new ArrayList<>();
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1);
        toSort.add(b.build());
        b.add(1);
        toSort.add(b.build());
        b = tf.distBuilder();
        b.add(1);
        b.add(2);
        toSort.add(b.build());
        Collections.sort(toSort);
        String actual = toSort.stream().map(Object::toString).collect(Collectors.joining(", "));
        List<String> expItems = new ArrayList<>();
        expItems.add("(1)");
        expItems.add("(1, 1)");
        expItems.add("(1, 2)");
        String expected = expItems.stream().collect(Collectors.joining(", "));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDistributionOfBoolean() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(false, 2);
        b.add(true, 3);
        Assert.assertEquals("(false, false, true, true, true)", b.build().toString());
    }

    @Test
    public void testDistributionOfDistribution() {
        DistributionBuilderInt2Bigint childBuilder = tf.distBuilder();
        Distribution empty = childBuilder.build();
        childBuilder = tf.distBuilder();
        childBuilder.add(1);
        childBuilder.add(2);
        childBuilder.addUnknown(2);
        Distribution filled = childBuilder.build();
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(empty, 2);
        b.add(filled, 3);
        String actual = b.build().format();
        List<String> expectedItems = new ArrayList<>();
        expectedItems.add("                      ()  2");
        expectedItems.add("(1, 2, unknown, unknown)  3");
        expectedItems.add("---------------------------");
        expectedItems.add("                   total  5");
        String expected = expectedItems.stream().collect(Collectors.joining("\n"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void whenEmptyDistributionNormalizedThenEmptyDistributionReturned() {
        Distribution empty = new Distribution.Builder().build();
        Assert.assertEquals(empty, empty.normalize());
    }

    @Test
    public void whenDistributionHasOnlyUnknownThenUnknownCountBecomesOne() {
        DistributionBuilderInt2Bigint inputBuilder = tf.distBuilder();
        inputBuilder.addUnknown(3);
        Distribution input = inputBuilder.build();
        Distribution normalized = input.normalize();
        Assert.assertTrue(! normalized.getItemIterator().hasNext());
        Assert.assertEquals(BigInteger.ONE, normalized.getCountUnknown());
    }

    @Test
    public void whenDistributionHasGcdThenNormalized() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(10, 2);
        b.add(11, 4);
        b.addUnknown(6);
        Distribution normalized = b.build().normalize();
        TestUtils.assertEqualsConvertingInt(1, normalized.getCountOf(new BigInteger("10")));
        TestUtils.assertEqualsConvertingInt(2, normalized.getCountOf(new BigInteger("11")));
        TestUtils.assertEqualsConvertingInt(3, normalized.getCountUnknown());
        TestUtils.assertEqualsConvertingInt(6, normalized.getTotal());
    }

    @Test
    public void whenIntegerScoredThenStoredAsBigInteger() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        Integer item = 5;
        b.add(item);
        Distribution d = b.build();
        boolean notStoredAsOrdinaryInteger = false;
        try {
            d.getCountOf(item);
        } catch(ClassCastException e) {
            notStoredAsOrdinaryInteger = true;
        }
        assertTrue(notStoredAsOrdinaryInteger);
        assertEquals(BigInteger.ONE, d.getCountOf(new BigInteger(item.toString())));
    }

    @Test
    public void whenDistributionSmallThenSortedAndAllInToString() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(new BigInteger("3"));
        b.add(new BigInteger("2"), new BigInteger("2"));
        b.add(new BigInteger("4"));
        b.add(new BigInteger("3"));
        assertEquals("(2, 2, 3, 3, 4)", b.build().toString());
    }

    @Test
    public void whenDistributionBigThenSortedAndTruncated() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(new BigInteger("5"), new BigInteger("1000000"));
        b.add(new BigInteger("2"));
        b.add(new BigInteger("3"));
        b.add(BigInteger.ONE);
        assertEquals("(1, 2, 3, 5, 5, 5, 5, 5, 5, 5, ...)", b.build().toString());
    }
}
