package com.github.mhdirkse.countlang.algorithm;

import static com.github.mhdirkse.countlang.algorithm.testtools.TestConstants.FOUR;
import static com.github.mhdirkse.countlang.algorithm.testtools.TestConstants.THREE;
import static com.github.mhdirkse.countlang.algorithm.testtools.TestConstants.TWO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory;
import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory.DistributionBuilderInt2Bigint;

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
        assertEquals(BigInteger.ONE, v.getValue());
        assertEquals(THREE, d.getCountOf(v));
        assertTrue(instance.hasNext());
        v = instance.next();
        assertFalse(v.isUnknown());
        assertEquals(TWO, v.getValue());
        assertEquals(FOUR, d.getCountOf(v));
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
        assertEquals(BigInteger.ONE, v.getValue());
        assertEquals(THREE, d.getCountOf(v));
        assertTrue(instance.hasNext());
        v = instance.next();
        assertTrue(v.isUnknown());
        assertEquals(FOUR, d.getCountOf(v));
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
        assertEquals(TWO, d.getCountOf(v));
        assertFalse(instance.hasNext());
    }

    @Test
    public void testForEmptyDistribution() {
        Distribution emptyDistribution = new Distribution.Builder().build();
        assertFalse(new PossibilityValueIterator(emptyDistribution).hasNext());
    }
}
