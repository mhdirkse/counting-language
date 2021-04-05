package com.github.mhdirkse.countlang.algorithm;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.ProgramException;

public class PossibilityCountingValidityStrategyTest {
    private PossibilityCountingValidityStrategy instance;
    private Distribution distributionOne;
    private Distribution distributionTwo;

    @Before
    public void setUp() {
        instance = new PossibilityCountingValidityStrategy.CountingPossibilities();
        Distribution.Builder b = new Distribution.Builder();
        b.add(1);
        distributionOne = b.build();
        b.add(2);
        distributionTwo = b.build();
    }

    @Test
    public void whenCountsMeaningfulThenNoError() {
        instance.startSampledVariable(distributionOne);

        instance.startSampledVariable(distributionTwo);
        instance.stopSampledVariable();
        instance.startSampledVariable(distributionTwo);
        instance.stopSampledVariable();

        instance.stopSampledVariable();
    }

    @Test(expected = ProgramException.class)
    public void whenConflictingCountsThenError() {
        instance.startSampledVariable(distributionOne);
        instance.startSampledVariable(distributionTwo);
        instance.stopSampledVariable();
        instance.startSampledVariable(distributionOne);
    }
}
