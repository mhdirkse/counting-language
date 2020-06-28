package com.github.mhdirkse.countlang.execution;

import org.junit.Assert;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.CountlangType;

public class SymbolTest {
    @Test
    public void whenSymbolCreatedThenUnknownTypeAndValueNull() {
        Symbol s = new Symbol("x");
        Assert.assertEquals("x", s.getName());
        Assert.assertEquals(CountlangType.UNKNOWN, s.getCountlangType());
        Assert.assertNull(s.getValue());
    }

    @Test
    public void whenSymbolInitializedToIntThenTypeBecomesInt() {
        Symbol s = new Symbol("x");
        s.setValue(CountlangType.INT.getExample());
        Assert.assertEquals(CountlangType.INT, s.getCountlangType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSymbolInitializedToNonCountlangValueThenExceptionThrown() {
        Symbol s = new Symbol("x");
        s.setValue(new Long(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSymbolChangesTypeThenExceptionThrown() {
        Symbol s = new Symbol("x");
        s.setValue(CountlangType.BOOL.getExample());
        s.setValue(CountlangType.INT.getExample());
    }
}
