package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PossibilitiesWalkerTest {
    private static final int ADDED_DISTRIBUTION_TOTAL = 5;
    private static final int EXTRA_REFINEMENT_FACTOR = 4;
    private static final int EXPECTED_TOTAL = ADDED_DISTRIBUTION_TOTAL * EXTRA_REFINEMENT_FACTOR;

    private static Distribution gedAddedDistribution() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(1, 2);
        b.add(2, 3);
        Distribution d = b.build();
        return d;
    }

    @Test
    public void whenAtStartThenAtStartTrueAndCountOne() throws Exception {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        assertFalse(instance.hasNext());
        assertEquals(1, instance.getCount());
        assertEquals(1, instance.getTotal());
        assertEquals(0, instance.getNumEdges());
    }

    @Test(expected = PossibilitiesWalkerException.class)
    public void whenDownWithEmptyDistributionThenError() throws Exception {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        instance.down(new Distribution.Builder().build());
    }

    @Test
    public void whenDownWithDistributionWhoseWeightDoesNotFitThenError() {
        boolean thrown = false;
        try {
            PossibilitiesWalker instance = new PossibilitiesWalker();
            Distribution.Builder b = new Distribution.Builder();
            b.add(1, 2);
            instance.down(b.build());
        } catch(PossibilitiesWalkerException e) {
            assertTrue(e instanceof PossibilitiesWalkerException.NewDistributionDoesNotFitParentCount);
            PossibilitiesWalkerException.NewDistributionDoesNotFitParentCount cast = (PossibilitiesWalkerException.NewDistributionDoesNotFitParentCount) e;
            assertEquals(2, cast.getChildCount());
            assertEquals(1, cast.getParentCount());
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void whenRefineAndThenDownWithFittingDistributionThenNoError() throws PossibilitiesWalkerException {
        PossibilitiesWalker instance = goDown();
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertEquals(1, instance.getNumEdges());
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

    private PossibilitiesWalker goDown() throws PossibilitiesWalkerException {
        Distribution d = gedAddedDistribution();
        PossibilitiesWalker instance = new PossibilitiesWalker();
        instance.refine(EXPECTED_TOTAL);
        assertEquals(EXPECTED_TOTAL, instance.getTotal());
        instance.down(d);
        return instance;
    }

    @Test(expected = PossibilitiesWalkerException.class)
    public void whenBeforeFirstDistributionValueThenAskingCountProducesError() throws Exception {
        PossibilitiesWalker instance = goDown();
        instance.getCount();
    }

    @Test
    public void whenBeforeFirstDistributionValueThenNumEdgesDoesNotCountNewAddedDistribution() throws Exception {
        PossibilitiesWalker instance = goDown();
        assertEquals(0, instance.getNumEdges());
    }
}
