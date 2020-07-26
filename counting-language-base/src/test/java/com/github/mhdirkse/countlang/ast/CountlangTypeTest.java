package com.github.mhdirkse.countlang.ast;

import org.junit.Assert;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.CountlangType;

public class CountlangTypeTest {
    @Test
    public void testIntegerGetsTypeInt() {
        Assert.assertEquals(CountlangType.INT, CountlangType.typeOf(new Integer(1)));
    }

    @Test
    public void testCountlangTypeIntMatchesAnInt() {
        Assert.assertTrue(CountlangType.INT.matches(new Integer(1).getClass()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLongValueIsNotAcceptedInCountlang() {
        CountlangType.typeOf(new Long(1));
    }

    @Test
    public void testIntDoesNotMatchLongValue() {
        Assert.assertFalse(CountlangType.INT.matches(Long.class));
    }

    @Test
    public void testAllCountlangTypesHaveCorrectExample() {
        for(CountlangType t : CountlangType.values()) {
            if(t == CountlangType.UNKNOWN) {
                continue;
            }
            Assert.assertTrue(t.matches(t.getExample().getClass()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenTypeOfNullThenExceptionThrown() {
        CountlangType.typeOf(null);
    }
}
