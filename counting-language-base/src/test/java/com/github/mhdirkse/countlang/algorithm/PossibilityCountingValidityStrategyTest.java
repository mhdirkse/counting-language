package com.github.mhdirkse.countlang.algorithm;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.mhdirkse.countlang.ast.ProgramException;

public class PossibilityCountingValidityStrategyTest {
    private PossibilityCountingValidityStrategy instance;
    private Distribution distributionOne;
    private Distribution distributionTwo;
    private Distribution distributionThree;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        instance = new PossibilityCountingValidityStrategy.CountingPossibilities();
        Distribution.Builder b = new Distribution.Builder();
        b.add(1);
        distributionOne = b.build();
        b.add(2);
        distributionTwo = b.build();
        b.add(3);
        distributionThree = b.build();
    }

    @Test
    public void whenCountsMeaningfulThenNoError() {
        instance.startSampledVariable(0, 0, distributionOne);

        instance.startSampledVariable(0, 0, distributionTwo);
        instance.stopSampledVariable();
        instance.startSampledVariable(0, 0, distributionTwo);
        instance.stopSampledVariable();

        instance.stopSampledVariable();
    }

    @Test
    public void whenConflictingCountsThenError() {
        thrown.expect(ProgramException.class);
        thrown.expectMessage("(5, 6): Tried to sample from 1 possibilities, but only 2 possibilities are allowed, because from that amount was sampled at (3, 4)");
        instance.startSampledVariable(1, 2, distributionOne);
        instance.startSampledVariable(3, 4, distributionTwo);
        instance.stopSampledVariable();
        instance.startSampledVariable(5, 6, distributionOne);
    }

    @Test
    public void whenCountMeaningfulThenNoError() {
        instance.startSampledVariable(0, 0, distributionOne);
        instance.startSampledVariable(0, 0, distributionTwo);
        instance.startSampledVariable(0, 0, distributionThree);
        instance.stopSampledVariable();
        instance.stopSampledVariable();
        instance.startSampledVariable(0, 0, distributionTwo);
        instance.startSampledVariable(0, 0, distributionThree);
    }
}
