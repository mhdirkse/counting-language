package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory;

public class DistributionCompareHelperTest {
    private static final BigInteger TWO = new BigInteger("2");
    private BigInteger someValue = new BigInteger("5");
    private BigInteger someOtherValue = new BigInteger("10");

    @Test
    public void whenDistributionEmptyThenDone() {
        Distribution d = new Distribution.Builder().build();
        DistributionCompareHelper instance = new DistributionCompareHelper(d);
        assertTrue(instance.getCountToNext().equals(BigInteger.ZERO));
        assertTrue(instance.isDone());
    }

    @Test
    public void whenDistributionOneElementThenOnceHaveCountOne() {
        Distribution d = new TestFactory().distBuilder()
                .add(someValue)
                .build();
        DistributionCompareHelper instance = new DistributionCompareHelper(d);
        assertEquals(BigInteger.ONE, instance.getCountToNext());
        assertFalse(instance.isDone());
        assertEquals(someValue, instance.getCurrent().getValue());
        // Getting the current value should not advance
        assertEquals(BigInteger.ONE, instance.getCountToNext());
        instance.advance(BigInteger.ONE);
        assertEquals(BigInteger.ZERO, instance.getCountToNext());
    }

    @Test
    public void whenDistributionOneElementTwiceThenCount2Count1() {
        Distribution d = new TestFactory().distBuilder()
                .add(someValue, TWO)
                .build();
        DistributionCompareHelper instance = new DistributionCompareHelper(d);
        assertEquals(TWO, instance.getCountToNext());
        assertEquals(someValue, instance.getCurrent().getValue());
        assertEquals(TWO, instance.getCountToNext());
        instance.advance(BigInteger.ONE);
        assertEquals(someValue, instance.getCurrent().getValue());
        assertEquals(BigInteger.ONE, instance.getCountToNext());
        instance.advance(BigInteger.ONE);
        assertEquals(BigInteger.ZERO, instance.getCountToNext());
    }

    @Test
    public void whenTwoDifferentValuesThenCount1Count1() {
        Distribution d = new TestFactory().distBuilder()
                .add(someValue)
                .add(someOtherValue)
                .build();
        DistributionCompareHelper instance = new DistributionCompareHelper(d);
        assertEquals(someValue, instance.getCurrent().getValue());
        assertEquals(BigInteger.ONE, instance.getCountToNext());
        instance.advance(BigInteger.ONE);
        assertEquals(someOtherValue, instance.getCurrent().getValue());
        assertEquals(BigInteger.ONE, instance.getCountToNext());
        instance.advance(BigInteger.ONE);
        assertEquals(BigInteger.ZERO, instance.getCountToNext());
    }
}
