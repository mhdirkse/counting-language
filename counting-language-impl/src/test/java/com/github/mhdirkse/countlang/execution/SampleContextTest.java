package com.github.mhdirkse.countlang.execution;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.types.Distribution;

public class SampleContextTest {
    private SampleContext instance;

    @Before
    public void setUp() {
        instance = new SampleContext();
    }

    @Test
    public void testAddingTwoSamplesFromSameBinomial() {
        sampleTwice(getBinomialDistribution());
        Assert.assertEquals(getSumOfTwoSamplesFromBinomial().format(), instance.getResult().format());
    }

    private void sampleTwice(Distribution d) {
        instance.startSampledVariable(d);
        while(instance.hasNextValue()) {
            int i1 = instance.nextValue();
            instance.startSampledVariable(d);
            while(instance.hasNextValue()) {
                int i2 = instance.nextValue();
                instance.score(i1 + i2);
            }
            instance.stopSampledVariable();
        }
        instance.stopSampledVariable();
    }

    @Test
    public void testHandlingUnknown() {
        sampleTwice(getDistributionWithUnknown());
        Assert.assertEquals(getSumOfTwoSamplesFromUnknown().format(), instance.getResult().format());
    }

    @Test
    public void testCorrectDistributionWeighing() {
        Distribution binomial = getBinomialDistribution();
        instance.startSampledVariable(binomial);
        while(instance.hasNextValue()) {
            int choice = instance.nextValue();
            if(choice == 1) {
                instance.startSampledVariable(binomial);
                while(instance.hasNextValue()) {
                    instance.score(instance.nextValue());
                }
                instance.stopSampledVariable();
            }
            else {
                Distribution uniform1To4 = getUniform1To4();
                instance.startSampledVariable(uniform1To4);
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
        instance.startSampledVariable(getBinomialDistribution());
        while(instance.hasNextValue()) {
            int choice = instance.nextValue();
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

    private Distribution getSumOfTwoSamplesFromUnknown() {
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
}
