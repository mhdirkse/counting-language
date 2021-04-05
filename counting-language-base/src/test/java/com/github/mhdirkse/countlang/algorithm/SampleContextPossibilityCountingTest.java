package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.mhdirkse.countlang.ast.ProgramException;

public class SampleContextPossibilityCountingTest {
    private SampleContext instance;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        instance = SampleContext.getInstance(true);
    }

    @Test
    public void whenDistributionHasCommonDenominatorThenNotSimplified() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(10, 2);
        b.add(11, 4);
        Distribution first = b.build();
        b = new Distribution.Builder();
        b.add(10, 3);
        b.add(11, 3);
        Distribution second = b.build();
        int v1 = 0;
        int v2 = 0;
        instance.startSampledVariable(0, 0, first);
        while(instance.hasNextValue()) {
            v1 = (Integer) instance.nextValue();
            instance.startSampledVariable(0, 0, second);
            while(instance.hasNextValue()) {
                v2 = (Integer) instance.nextValue();
                instance.score(v1 + v2);
            }
            instance.stopSampledVariable();
        }
        instance.stopSampledVariable();
        Distribution result = instance.getResult();
        b = new Distribution.Builder();
        b.add(20, 6);
        b.add(21, 6 + 12);
        b.add(22, 12);
        assertEquals(b.build().format(), result.format());
    }

    @Test
    public void whenDistributionCountMismatchThenError() {
        expectedException.expect(ProgramException.class);
        expectedException.expectMessage("(9, 10): Tried to sample from 5 possibilities, but only 4 possibilities are allowed, because from that amount was sampled at (5, 6)");
        Distribution.Builder b = new Distribution.Builder();
        b.add(1);
        b.add(2);
        Distribution first = b.build();
        b.add(3);
        Distribution second = b.build();
        b.add(4);
        Distribution third = b.build();
        b.add(5);
        Distribution fourth = b.build();
        instance.startSampledVariable(1, 2, first);
        instance.nextValue();
        instance.startSampledVariable(3, 4, second);
        instance.nextValue();
        instance.startSampledVariable(5, 6, third);
        while(instance.hasNextValue()) {
            instance.nextValue();
            instance.scoreUnknown();
        }
        instance.stopSampledVariable();
        while(instance.hasNextValue()) {
            instance.nextValue();
            instance.scoreUnknown();
        }
        instance.stopSampledVariable();
        instance.nextValue();
        instance.startSampledVariable(7, 8, second);
        instance.nextValue();
        instance.startSampledVariable(9, 10, fourth);
    }
}
