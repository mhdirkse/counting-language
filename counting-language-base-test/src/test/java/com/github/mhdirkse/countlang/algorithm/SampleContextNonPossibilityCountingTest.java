/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory;
import com.github.mhdirkse.countlang.algorithm.testtools.TestFactory.DistributionBuilderInt2Bigint;

public class SampleContextNonPossibilityCountingTest {
    private TestFactory tf;
    private SampleContextImpl instance;

    @Before
    public void setUp() {
        tf = new TestFactory();
        instance = (SampleContextImpl) SampleContext.getInstance(false);
    }

    @Test
    public void testAddingTwoSamplesFromSameBinomial() {
        sampleTwice(getBinomialDistribution());
        Assert.assertEquals(getSumOfTwoSamplesFromBinomial().format(), instance.getResult().format());
    }

    private void sampleTwice(Distribution d1, Distribution d2) {
        instance.startSampledVariable(0, 0, d1);
        while(instance.hasNextValue()) {
            ProbabilityTreeValue i1 = instance.nextValue();
            if(i1.isUnknown()) {
                instance.scoreUnknown();
                continue;
            }
            instance.startSampledVariable(0, 0, d2);
            while(instance.hasNextValue()) {
                ProbabilityTreeValue i2 = instance.nextValue();
                if(i2.isUnknown()) {
                    instance.scoreUnknown();
                    continue;
                }
                instance.score(((BigInteger) i1.getValue()).add((BigInteger) i2.getValue()));
            }
            instance.stopSampledVariable();
        }
        instance.stopSampledVariable();
    }

    private void sampleTwice(Distribution d) {
        sampleTwice(d, d);
    }

    @Test
    public void testHandlingUnknown() {
        sampleTwice(getDistributionWithUnknown());
        Assert.assertEquals(getSumOfTwoDistributionWithUnknown().format(), instance.getResult().format());
    }

    @Test
    public void testCorrectDistributionWeighing() {
        Distribution binomial = getBinomialDistribution();
        instance.startSampledVariable(0, 0, binomial);
        while(instance.hasNextValue()) {
            BigInteger choice = (BigInteger) instance.nextValue().getValue();
            if(choice.equals(BigInteger.ONE)) {
                instance.startSampledVariable(0, 0, binomial);
                while(instance.hasNextValue()) {
                    instance.score(instance.nextValue().getValue());
                }
                instance.stopSampledVariable();
            }
            else {
                Distribution uniform1To4 = getUniform1To4();
                instance.startSampledVariable(0, 0, uniform1To4);
                while(instance.hasNextValue()) {
                    instance.score(instance.nextValue().getValue());
                }
                instance.stopSampledVariable();
            }
        }
        instance.stopSampledVariable();
        Assert.assertEquals(getSampleBinomialOrUniform1To4().format(), instance.getResult().format());
    }

    @Test
    public void testScoringUnknown() {
        instance.startSampledVariable(0, 0, getBinomialDistribution());
        while(instance.hasNextValue()) {
            BigInteger choice = (BigInteger) instance.nextValue().getValue();
            if(choice.equals(BigInteger.ONE)) {
                instance.score(choice);
            }
            else {
                instance.scoreUnknown();
            }
        }
        instance.stopSampledVariable();
        Assert.assertEquals(getDistributionWithUnknown().format(), instance.getResult().format());
    }

    @Test
    public void whenScoredWithoutSamplingThenSingletonDistribution() {
        instance.score(3);
        Assert.assertEquals(getSingletonDistribution().format(), instance.getResult().format());
    }

    @Test
    public void whenScoredUnknownWithoutSamplingThenSingletonDistributionWithUnknown() {
        instance.scoreUnknown();
        Assert.assertEquals(getSingletonDistributionWithUnknown().format(), instance.getResult().format());
    }

    @Test
    public void testPluralDistributionsWithoutUnknown() {
        sampleTwice(getDistributionPlural());
        Assert.assertEquals(getDistributionSumOfTwoPlural().format(), instance.getResult().format());
    }

    @Test
    public void testPluralDistributionWithUnknown() {
        sampleTwice(getDistributionPluralWithUnknown());
        Assert.assertEquals(getDistributionSumOfTwoPluralWithUnknown().format(), instance.getResult().format());
    }

    @Test
    public void testPluralDistributionWithUnknown_2() {
        sampleTwice(getDistributionPluralWithUnknown(), getDistributionPluralMultipleUnknown());
        Assert.assertEquals(
                getDistributionPluralWithUnknownPlusDistributionPluralMultipleUnknown().format(),
                instance.getResult().format());
    }

    @Test
    public void testPluralDistributionWithUnknown_3() {
        sampleTwice(getDistributionPluralMultipleUnknown(), getDistributionPluralWithUnknown());
        Assert.assertEquals(
                getDistributionPluralWithUnknownPlusDistributionPluralMultipleUnknown().format(),
                instance.getResult().format());
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

    private Distribution getBinomialDistribution() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1);
        b.add(2);
        return b.build();
    }

    private Distribution getSumOfTwoSamplesFromBinomial() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(2);
        b.add(3);
        b.add(3);
        b.add(4);
        return b.build();
    }

    private Distribution getDistributionWithUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1);
        b.addUnknown(1);
        return b.build();
    }

    private Distribution getSumOfTwoDistributionWithUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(2);
        b.addUnknown(3);
        return b.build();
    }

    private Distribution getUniform1To4() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        for(int i = 1; i <= 4; i++) {
            b.add(i);
        }
        return b.build();
    }

    private Distribution getSampleBinomialOrUniform1To4() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1, 3);
        b.add(2, 3);
        b.add(3);
        b.add(4);
        return b.build();
    }

    private Distribution getSingletonDistribution() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(3);
        return b.build();
    }

    private Distribution getSingletonDistributionWithUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.addUnknown(1);
        return b.build();
    }

    private Distribution getDistributionPlural() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1, 3);
        b.add(2, 2);
        b.add(3);
        return b.build();
    }

    private Distribution getDistributionSumOfTwoPlural() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(2, 9);
        b.add(3, 12);
        b.add(4, 10);
        b.add(5, 4);
        b.add(6, 1);
        return b.build();
    }

    private Distribution getDistributionPluralWithUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(1, 3);
        b.add(2, 2);
        b.addUnknown(1);
        return b.build();
    }

    private Distribution getDistributionSumOfTwoPluralWithUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(2, 9);
        b.add(3, 12);
        b.add(4, 4);
        b.addUnknown(11);
        return b.build();
    }

    private Distribution getDistributionPluralMultipleUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.addUnknown(3);
        b.add(2, 2);
        b.add(3, 1);
        return b.build();
    }

    private Distribution getDistributionPluralWithUnknownPlusDistributionPluralMultipleUnknown() {
        DistributionBuilderInt2Bigint b = tf.distBuilder();
        b.add(3, 6);
        b.add(4, 7);
        b.add(5, 2);
        b.addUnknown(21);
        return b.build();
    }
}
