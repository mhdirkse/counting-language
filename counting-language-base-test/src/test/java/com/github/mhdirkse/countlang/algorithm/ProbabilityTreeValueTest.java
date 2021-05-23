package com.github.mhdirkse.countlang.algorithm;

import static com.github.mhdirkse.countlang.algorithm.testtools.TestConstants.TWO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

public class ProbabilityTreeValueTest {
    @Test
    public void testProbabilityValue() {
        ProbabilityTreeValue one = ProbabilityTreeValue.of(BigInteger.ONE);
        ProbabilityTreeValue two = ProbabilityTreeValue.of(TWO);
        ProbabilityTreeValue unknown = ProbabilityTreeValue.unknown();
        assertEquals(0, one.compareTo(one));
        assertEquals(0, unknown.compareTo(unknown));
        assertEquals(-1, one.compareTo(unknown));
        assertEquals(1, unknown.compareTo(one));
        assertTrue(one.compareTo(two) < 0);
        assertTrue(two.compareTo(one) > 0);
    }
}
