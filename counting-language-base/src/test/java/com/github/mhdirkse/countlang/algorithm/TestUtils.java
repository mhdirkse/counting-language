package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

public final class TestUtils {
    private TestUtils() {
    }

    public static void assertEqualsConvertingInt(int expected, BigInteger actual) {
        BigInteger bigExpected = new BigInteger(Integer.valueOf(expected).toString());
        assertEquals(bigExpected, actual);
    }

    public static void assertEqualsConvertingInt(int expected, Object actual) {
        BigInteger bigExpected = new BigInteger(Integer.valueOf(expected).toString());
        assertEquals(bigExpected, actual);
    }
}
