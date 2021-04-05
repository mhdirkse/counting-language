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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.algorithm.SampleContextImpl;

public class SampleContextNonPossibilityCountingTest {
    private SampleContextImpl instance;

    @Before
    public void setUp() {
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
            int i1 = (Integer) instance.nextValue();
            instance.startSampledVariable(0, 0, d2);
            while(instance.hasNextValue()) {
                int i2 = (Integer) instance.nextValue();
                instance.score(i1 + i2);
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
            int choice = (Integer) instance.nextValue();
            if(choice == 1) {
                instance.startSampledVariable(0, 0, binomial);
                while(instance.hasNextValue()) {
                    instance.score(instance.nextValue());
                }
                instance.stopSampledVariable();
            }
            else {
                Distribution uniform1To4 = getUniform1To4();
                instance.startSampledVariable(0, 0, uniform1To4);
                while(instance.hasNextValue()) {
                    instance.score(instance.nextValue());
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
            int choice = (Integer) instance.nextValue();
            if(choice == 1) {
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

    private Distribution getBinomialDistribution() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(1);
        b.add(2);
        return b.build();
    }

    private Distribution getSumOfTwoSamplesFromBinomial() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(2);
        b.add(3);
        b.add(3);
        b.add(4);
        return b.build();
    }

    private Distribution getDistributionWithUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(1);
        b.addUnknown(1);
        return b.build();
    }

    private Distribution getSumOfTwoDistributionWithUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(2);
        b.addUnknown(3);
        return b.build();
    }

    private Distribution getUniform1To4() {
        Distribution.Builder b = new Distribution.Builder();
        for(int i = 1; i <= 4; i++) {
            b.add(i);
        }
        return b.build();
    }

    private Distribution getSampleBinomialOrUniform1To4() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(1, 3);
        b.add(2, 3);
        b.add(3);
        b.add(4);
        return b.build();
    }

    private Distribution getSingletonDistribution() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(3);
        return b.build();
    }

    private Distribution getSingletonDistributionWithUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.addUnknown(1);
        return b.build();
    }

    private Distribution getDistributionPlural() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(1, 3);
        b.add(2, 2);
        b.add(3);
        return b.build();
    }

    private Distribution getDistributionSumOfTwoPlural() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(2, 9);
        b.add(3, 12);
        b.add(4, 10);
        b.add(5, 4);
        b.add(6, 1);
        return b.build();
    }

    private Distribution getDistributionPluralWithUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(1, 3);
        b.add(2, 2);
        b.addUnknown(1);
        return b.build();
    }

    private Distribution getDistributionSumOfTwoPluralWithUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(2, 9);
        b.add(3, 12);
        b.add(4, 4);
        b.addUnknown(11);
        return b.build();
    }

    private Distribution getDistributionPluralMultipleUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.addUnknown(3);
        b.add(2, 2);
        b.add(3, 1);
        return b.build();
    }

    private Distribution getDistributionPluralWithUnknownPlusDistributionPluralMultipleUnknown() {
        Distribution.Builder b = new Distribution.Builder();
        b.add(3, 6);
        b.add(4, 7);
        b.add(5, 2);
        b.addUnknown(21);
        return b.build();
    }
}
