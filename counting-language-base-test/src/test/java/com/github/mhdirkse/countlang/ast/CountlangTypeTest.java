package com.github.mhdirkse.countlang.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class CountlangTypeTest {
    @Test
    public void primitiveTypeHasGeneralizationAny() {
        CountlangType t = CountlangType.integer();
        List<CountlangType> gen = t.getGeneralizations();
        assertEquals(1, gen.size());
        assertSame(CountlangType.any(), gen.get(0));
        t = CountlangType.fraction();
        gen = t.getGeneralizations();
        assertEquals(1, gen.size());
        assertSame(CountlangType.any(), gen.get(0));
    }

    @Test
    public void wildcardHasNoGeneralization() {
        assertEquals(0, CountlangType.any().getGeneralizations().size());
    }

    @Test
    public void distributionOfPrimitiveHasGeneralization() {
        CountlangType t = CountlangType.distributionOf(CountlangType.integer());
        List<CountlangType> gen = t.getGeneralizations();
        assertEquals(1, gen.size());
        assertSame(CountlangType.distributionOfAny(), gen.get(0));
    }

    @Test
    public void testIsPrimitiveNumeric() {
    	assertTrue(CountlangType.fraction().isPrimitiveNumeric());
    	assertTrue(CountlangType.integer().isPrimitiveNumeric());
    	assertFalse(CountlangType.bool().isPrimitiveNumeric());
    	assertFalse(CountlangType.distributionOf(CountlangType.integer()).isPrimitiveNumeric());
    }
}
