package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory;
import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory.DistributionBuilderInt2Bigint;
import com.github.mhdirkse.countlang.ast.ProgramException;

public class SampleContextPossibilityCountingTest {
    private TestFactory tf;
    private SampleContext instance;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        tf = new TestFactory();
        instance = SampleContext.getInstance(true);
    }

    @Test
    public void whenDistributionHasCommonDenominatorThenNotSimplified() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(10, 2);
        b.add(11, 4);
        Distribution first = b.build();
        b = tf.distBuilder();
        b.add(10, 3);
        b.add(11, 3);
        Distribution second = b.build();
        BigInteger v1 = BigInteger.ZERO;
        BigInteger v2 = BigInteger.ZERO;
        instance.startSampledVariable(0, 0, first);
        while(instance.hasNextValue()) {
            v1 = (BigInteger) instance.nextValue().getValue();
            instance.startSampledVariable(0, 0, second);
            while(instance.hasNextValue()) {
                v2 = (BigInteger) instance.nextValue().getValue();
                instance.score(v1.add(v2));
            }
            instance.stopSampledVariable();
        }
        instance.stopSampledVariable();
        Distribution result = instance.getResult();
        b = tf.distBuilder();
        b.add(20, 6);
        b.add(21, 6 + 12);
        b.add(22, 12);
        assertEquals(b.build().format(), result.format());
    }

    @Test
    public void whenDistributionCountMismatchThenError() {
        expectedException.expect(ProgramException.class);
        expectedException.expectMessage("(9, 10): Tried to sample from 5 possibilities, but only 4 possibilities are allowed, because from that amount was sampled at (5, 6)");
        DistributionBuilderInt2Bigint b = tf.distBuilder();
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

    @Test
    public void whenNotSampledOnlyScoredThenOnePossibility() {
        instance.score(1);
        Distribution actualResult = instance.getResult();
        DistributionBuilderInt2Bigint expected = tf.distBuilder();
        expected.add(1);
        assertEquals(expected.build().format(), actualResult.format());
    }

    @Test
    public void whenSampledDistributionsHaveUnknownThenUnknownHandledCorrectly() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1);
        b.addUnknown(1);
        Distribution firstInput = b.build();
        b.addUnknown(1);
        Distribution secondInput = b.build();
        instance.startSampledVariable(0, 0, firstInput);
        while(instance.hasNextValue()) {
            ProbabilityTreeValue i1 = instance.nextValue();
            if(i1.isUnknown()) {
                instance.scoreUnknown();
                continue;
            }
            instance.startSampledVariable(0, 0, secondInput);
            while(instance.hasNextValue()) {
                ProbabilityTreeValue i2 = instance.nextValue();
                if(i2.isUnknown()) {
                    instance.scoreUnknown();
                    continue;
                }
                instance.score(1);
            }
            instance.stopSampledVariable();
        }
        instance.stopSampledVariable();
        Distribution actualResult = instance.getResult();
        DistributionBuilderInt2Bigint expected = tf.distBuilder();
        expected.add(1);
        expected.addUnknown(5);
        assertEquals(expected.build().format(), actualResult.format());
    }
}
