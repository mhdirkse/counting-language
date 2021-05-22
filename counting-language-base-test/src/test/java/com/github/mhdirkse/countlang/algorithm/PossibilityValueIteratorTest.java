package com.github.mhdirkse.countlang.algorithm;

import static com.github.mhdirkse.countlang.algorithm.TestUtils.assertEqualsConvertingInt;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.TestFactory.DistributionBuilderInt2Bigint;

public class PossibilityValueIteratorTest {
    private TestFactory tf;

    @Before
    public void setUp() {
        tf = new TestFactory();
    }

    @Test
    public void testForDistributionWithoutUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1, 3);
        b.add(2, 4);
        Distribution d = b.build();
        PossibilityValueIterator instance = new PossibilityValueIterator(d);
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertFalse(v.isUnknown());
        assertEqualsConvertingInt(1, v.getValue());
        assertEqualsConvertingInt(3, d.getCountOf(v));
        assertTrue(instance.hasNext());
        v = instance.next();
        assertFalse(v.isUnknown());
        assertEqualsConvertingInt(2, v.getValue());
        assertEqualsConvertingInt(4, d.getCountOf(v));
        assertFalse(instance.hasNext());
    }

    @Test
    public void testForDistributionWithUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1, 3);
        b.addUnknown(4);
        Distribution d = b.build();
        PossibilityValueIterator instance = new PossibilityValueIterator(d);
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertFalse(v.isUnknown());
        assertEqualsConvertingInt(1, v.getValue());
        assertEqualsConvertingInt(3, d.getCountOf(v));
        assertTrue(instance.hasNext());
        v = instance.next();
        assertTrue(v.isUnknown());
        assertEqualsConvertingInt(4, d.getCountOf(v));
        assertFalse(instance.hasNext());
    }

    @Test
    public void testForDistributionWithOnlyUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.addUnknown(2);
        Distribution d = b.build();
        PossibilityValueIterator instance = new PossibilityValueIterator(d);
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertTrue(v.isUnknown());
        assertEqualsConvertingInt(2, d.getCountOf(v));
        assertFalse(instance.hasNext());
    }

    @Test
    public void testForEmptyDistribution() {
        Distribution emptyDistribution = new Distribution.Builder().build();
        assertFalse(new PossibilityValueIterator(emptyDistribution).hasNext());
    }
}
