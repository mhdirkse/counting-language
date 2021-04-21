package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PossibilityValueIteratorTest {
    @Test
    public void testForDistributionWithoutUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(1, 3);
        b.add(2, 4);
        Distribution d = b.build();
        PossibilityValueIterator instance = new PossibilityValueIterator(d);
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertFalse(v.isUnknown());
        assertEquals(1, v.getValue());
        assertEquals(3, d.getCountOf(v));
        assertTrue(instance.hasNext());
        v = instance.next();
        assertFalse(v.isUnknown());
        assertEquals(2, v.getValue());
        assertEquals(4, d.getCountOf(v));
        assertFalse(instance.hasNext());
    }

    @Test
    public void testForDistributionWithUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(1, 3);
        b.addUnknown(4);
        Distribution d = b.build();
        PossibilityValueIterator instance = new PossibilityValueIterator(d);
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertFalse(v.isUnknown());
        assertEquals(1, v.getValue());
        assertEquals(3, d.getCountOf(v));
        assertTrue(instance.hasNext());
        v = instance.next();
        assertTrue(v.isUnknown());
        assertEquals(4, d.getCountOf(v));
        assertFalse(instance.hasNext());
    }

    @Test
    public void testForDistributionWithOnlyUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.addUnknown(2);
        Distribution d = b.build();
        PossibilityValueIterator instance = new PossibilityValueIterator(d);
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertTrue(v.isUnknown());
        assertEquals(2, d.getCountOf(v));
        assertFalse(instance.hasNext());
    }

    @Test
    public void testForEmptyDistribution() {
        Distribution emptyDistribution = new Distribution.Builder().build();
        assertFalse(new PossibilityValueIterator(emptyDistribution).hasNext());
    }
}
