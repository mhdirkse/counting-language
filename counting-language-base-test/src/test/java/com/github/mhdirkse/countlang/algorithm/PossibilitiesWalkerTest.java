package com.github.mhdirkse.countlang.algorithm;

import static com.github.mhdirkse.countlang.algorithm.TestUtils.assertEqualsConvertingInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory;
import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory.DistributionBuilderInt2Bigint;

public class PossibilitiesWalkerTest {
    private static final int ADDED_DISTRIBUTION_TOTAL = 5;
    private static final int EXTRA_REFINEMENT_FACTOR = 4;
    private static final int EXPECTED_TOTAL = ADDED_DISTRIBUTION_TOTAL * EXTRA_REFINEMENT_FACTOR;

    private TestFactory tf;

    @Before
    public void setUp() {
        tf = new TestFactory();
    }

    private Distribution getAddedDistribution() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1, 2);
        b.add(2, 3);
        Distribution d = b.build();
        return d;
    }

    private Distribution getAddedDistributionWithUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1, 2);
        b.addUnknown(3);
        Distribution d = b.build();
        return d;        
    }

    private Distribution getNextAddedDistributionWithUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(10, 1);
        b.addUnknown(3);
        return b.build();
    }

    @Test
    public void whenAtStartThenCountOne() {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        assertFalse(instance.hasNext());
        assertEqualsConvertingInt(1, instance.getCount());
        assertEqualsConvertingInt(1, instance.getTotal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenDownWithEmptyDistributionThenError() throws Exception {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        instance.down(new Distribution.Builder().build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenDownWithDistributionWhoseWeightDoesNotFitThenError() {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1, 2);
        instance.down(b.build());
    }

    @Test
    public void whenRefineAndThenDownWithFittingDistributionThenNoError() throws Exception {
        PossibilitiesWalker instance = goDown(getAddedDistribution());
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertFalse(v.isUnknown());
        assertEqualsConvertingInt(1, v.getValue());
        assertEqualsConvertingInt(2 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertTrue(instance.hasNext());
        v = instance.next();
        assertFalse(v.isUnknown());
        assertEqualsConvertingInt(2, v.getValue());
        assertEqualsConvertingInt(3 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertFalse(instance.hasNext());
        instance.up();
        assertFalse(instance.hasNext());
    }

    private PossibilitiesWalker goDown(Distribution d) throws PossibilitiesWalkerException {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        instance.refine(EXPECTED_TOTAL);
        assertEqualsConvertingInt(EXPECTED_TOTAL, instance.getTotal());
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
        assertEqualsConvertingInt(1, v.getValue());
        assertEqualsConvertingInt(2 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertTrue(instance.hasNext());
        v = instance.next();
        assertTrue(v.isUnknown());
        assertEqualsConvertingInt(3 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertFalse(instance.hasNext());
    }

    @Test
    public void whenRefinementDuringIteratingThenCountsAndTotalAdjusted() throws Exception {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        Distribution d = getAddedDistribution();
        instance.refine(d.getTotal());
        assertEqualsConvertingInt(ADDED_DISTRIBUTION_TOTAL, instance.getTotal());
        instance.down(d);
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertFalse(v.isUnknown());
        assertEqualsConvertingInt(1, v.getValue());
        assertEqualsConvertingInt(2, instance.getCount());
        instance.refine(EXTRA_REFINEMENT_FACTOR);
        assertEqualsConvertingInt(EXPECTED_TOTAL, instance.getTotal());
        assertEqualsConvertingInt(2 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertTrue(instance.hasNext());
        v = instance.next();
        assertEqualsConvertingInt(2, v.getValue());
        assertEqualsConvertingInt(3 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertFalse(instance.hasNext());
    }

    @Test
    public void whenProbabilityTreeHasManyLevelsThenRefiningAndCountingRemainsConsistent() throws Exception {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        Distribution d1 = getAddedDistribution();
        instance.refine(d1.getTotal());
        assertEqualsConvertingInt(ADDED_DISTRIBUTION_TOTAL, instance.getTotal());
        instance.down(d1);
        assertTrue(instance.hasNext());
        instance.refine(EXTRA_REFINEMENT_FACTOR);
        assertEqualsConvertingInt(EXPECTED_TOTAL, instance.getTotal());
        Distribution d2 = getNextAddedDistributionWithUnknown();
        instance.next();
        instance.down(d2);
        assertTrue(instance.hasNext());
        ProbabilityTreeValue v = instance.next();
        assertEqualsConvertingInt(10, v.getValue());
        assertEqualsConvertingInt(2, instance.getCount());
        assertTrue(instance.hasNext());
        v = instance.next();
        assertTrue(v.isUnknown());
        assertEqualsConvertingInt(6, instance.getCount());
        assertFalse(instance.hasNext());
        instance.up();
        assertEqualsConvertingInt(2 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertTrue(instance.hasNext());
        v = instance.next();
        assertFalse(v.isUnknown());
        assertEqualsConvertingInt(2, v.getValue());
        assertEqualsConvertingInt(3 * EXTRA_REFINEMENT_FACTOR, instance.getCount());
        assertFalse(instance.hasNext());
    }

    public void whenRefiningOverflowsIntegerRangeThenNoError() throws Exception {
        PossibilitiesWalker instance = new PossibilitiesWalker();
        instance.refine(1000000);
        instance.refine(1000000);
        assertEquals(new BigInteger("1000000000000"), instance.getTotal());
    }
}
