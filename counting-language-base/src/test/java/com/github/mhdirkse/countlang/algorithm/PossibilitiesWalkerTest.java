package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PossibilitiesWalkerTest {
    @Test
    public void whenAtRootThenAtRootTrueAndCountOne() {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        assertTrue(instance.isAtRoot());
        assertTrue(instance.isAtNewLeaf());
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
}
