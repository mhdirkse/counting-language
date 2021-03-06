package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PossibilitiesWalkerTest {
    private static final int ADDED_DISTRIBUTION_TOTAL = 5;
    private static final int EXTRA_REFINEMENT_FACTOR = 4;
    private static final int EXPECTED_TOTAL = ADDED_DISTRIBUTION_TOTAL * EXTRA_REFINEMENT_FACTOR;

    private static Distribution getAddedDistribution() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(1, 2);
        b.add(2, 3);
        Distribution d = b.build();
        return d;
    }

    private static Distribution getAddedDistributionWithUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(1, 2);
        b.addUnknown(3);
        Distribution d = b.build();
        return d;        
    }

    private static Distribution getNextAddedDistributionWithUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(10, 1);
        b.addUnknown(3);
        return b.build();
    }

    @Test
    public void whenAtStartThenCountOne() {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        assertFalse(instance.hasNext());
        assertEquals(1, instance.getCount());
        assertEquals(1, instance.getTotal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenDownWithEmptyDistributionThenError() throws Exception {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        instance.down(new Distribution.Builder().build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenDownWithDistributionWhoseWeightDoesNotFitThenError() {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        Distribution.Builder b = new Distribution.Builder();
        b.add(1, 2);
        instance.down(b.build());
    }

    @Test
    public void whenRefineAndThenDownWithFittingDistributionThenNoError() throws Exception {
        PossibilitiesWalker instance = goDown(getAddedDistribution());
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertFalse(v.isUnknown());
        assertEquals(1, v.getValue());
        assertEquals(2 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertTrue(instance.hasNext());
        v = instance.next();
        assertFalse(v.isUnknown());
        assertEquals(2, v.getValue());
        assertEquals(3 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertFalse(instance.hasNext());
        instance.up();
        assertFalse(instance.hasNext());
    }

    private PossibilitiesWalker goDown(Distribution d) throws PossibilitiesWalkerException {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        instance.refine(EXPECTED_TOTAL);
        assertEquals(EXPECTED_TOTAL, instance.getTotal());
        instance.down(d);
        return instance;
    }

    @Test(expected = IllegalStateException.class)
    public void whenBeforeFirstDistributionValueThenAskingCountProducesError() throws Exception {
        PossibilitiesWalker instance = goDown(getAddedDistribution());
        instance.getCount();
    }

    @Test
    public void whenAddedDistributionHasUnknownThenUnknownCovered() throws Exception {
        PossibilitiesWalker instance = goDown(getAddedDistributionWithUnknown());
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertFalse(v.isUnknown());
        assertEquals(1, v.getValue());
        assertEquals(2 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertTrue(instance.hasNext());
        v = instance.next();
        assertTrue(v.isUnknown());
        assertEquals(3 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertFalse(instance.hasNext());
    }

    @Test
    public void whenRefinementDuringIteratingThenCountsAndTotalAdjusted() throws Exception {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        Distribution d = getAddedDistribution();
        instance.refine(d.getTotal());
        assertEquals(ADDED_DISTRIBUTION_TOTAL, instance.getTotal());
        instance.down(d);
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertFalse(v.isUnknown());
        assertEquals(1, v.getValue());
        assertEquals(2, instance.getCount());
        instance.refine(EXTRA_REFINEMENT_FACTOR);
        assertEquals(EXPECTED_TOTAL, instance.getTotal());
        assertEquals(2 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertTrue(instance.hasNext());
        v = instance.next();
        assertEquals(2, v.getValue());
        assertEquals(3 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertFalse(instance.hasNext());
    }

    @Test
    public void whenProbabilityTreeHasManyLevelsThenRefiningAndCountingRemainsConsistent() throws Exception {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        Distribution d1 = getAddedDistribution();
        instance.refine(d1.getTotal());
        assertEquals(ADDED_DISTRIBUTION_TOTAL, instance.getTotal());
        instance.down(d1);
        assertTrue(instance.hasNext());
        instance.refine(EXTRA_REFINEMENT_FACTOR);
        assertEquals(EXPECTED_TOTAL, instance.getTotal());
        Distribution d2 = getNextAddedDistributionWithUnknown();
        instance.next();
        instance.down(d2);
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertEquals(10, v.getValue());
        assertEquals(2, instance.getCount());
        assertTrue(instance.hasNext());
        v = instance.next();
        assertTrue(v.isUnknown());
        assertEquals(6, instance.getCount());
        assertFalse(instance.hasNext());
        instance.up();
        assertEquals(2 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertTrue(instance.hasNext());
        v = instance.next();
        assertFalse(v.isUnknown());
        assertEquals(2, v.getValue());
        assertEquals(3 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertFalse(instance.hasNext());
    }

    @Test(expected = PossibilitiesWalkerException.class)
    public void whenRefiningHasOverflowThenError() throws Exception {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        instance.refine(1000000);
        instance.refine(1000000);
    }
}
