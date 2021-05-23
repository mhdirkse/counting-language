package com.github.mhdirkse.countlang.algorithm.testtools;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import com.github.mhdirkse.countlang.algorithm.Distribution;

public class TestFactory {
    public DistributionBuilderInt2Bigint distBuilder() {
        return new DistributionBuilderInt2Bigint(new Distribution.Builder());
    }

    public static class DistributionBuilderInt2Bigint {
        private final Distribution.Builder delegate;

        public DistributionBuilderInt2Bigint(Distribution.Builder delegate) {
            this.delegate = delegate;
        }

        private Object int2Bigint(Object original) {
            if(original instanceof Integer) {
                return new BigInteger(((Integer) original).toString());
            } else {
                return original;
            }
        }

        private Object int2Bigint(int value) {
            return new BigInteger(Integer.valueOf(value).toString());
        }

        public DistributionBuilderInt2Bigint add(int item) {
            delegate.add(int2Bigint(item));
            return this;
        }

        public DistributionBuilderInt2Bigint add(int item, int count) {
            delegate.add(int2Bigint(item), (BigInteger) int2Bigint(count));
            return this;
        }

        public DistributionBuilderInt2Bigint add(Object item) {
            delegate.add(int2Bigint(item));
            return this;
        }

        public DistributionBuilderInt2Bigint add(Object item, int count) {
            delegate.add(int2Bigint(item), (BigInteger) int2Bigint(count));
            return this;
        }

        public DistributionBuilderInt2Bigint add(Object item, BigInteger count) {
            delegate.add(int2Bigint(item), (BigInteger) int2Bigint(count));
            return this;
        }

        public DistributionBuilderInt2Bigint addUnknown(int countOfUnknown) {
            delegate.addUnknown((BigInteger) int2Bigint(countOfUnknown));
            return this;
        }

        public DistributionBuilderInt2Bigint addUnknown(BigInteger countOfUnknown) {
            delegate.addUnknown(countOfUnknown);
            return this;
        }

        public DistributionBuilderInt2Bigint refine(int factor) {
            delegate.refine((BigInteger) int2Bigint(factor));
            return this;
        }

        public DistributionBuilderInt2Bigint refine(BigInteger factor) {
            delegate.refine(factor);
            return this;
        }

        public void expectTotal(int total) {
            assertEquals(new BigInteger(Integer.valueOf(total).toString()), delegate.getTotal());
        }

        public void expectTotal(BigInteger total) {
            assertEquals(total, delegate.getTotal());
        }

        public void expectEquals(Distribution distribution) {
            Distribution actual = delegate.build();
            if(actual.equals(distribution)) {
                return;
            }
            assertEquals(distribution.format(), actual.format());
        }

        public Distribution build() {
            return delegate.build();
        }
    }
}