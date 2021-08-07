package com.github.mhdirkse.countlang.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.github.mhdirkse.countlang.type.CountlangType;
import com.github.mhdirkse.countlang.type.TupleType;

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

    @Test
    public void testTupleOfTwoRemainsUnique() {
        TupleType firstInstance = CountlangType.tupleOf(Arrays.asList(CountlangType.integer(), CountlangType.bool()));
        TupleType secondInstance = CountlangType.tupleOf(Arrays.asList(CountlangType.integer(), CountlangType.bool()));
        assertSame(firstInstance, secondInstance);
        assertTrue(firstInstance.isTuple());
        List<CountlangType> storedSubTypes = firstInstance.getTupleSubTypes();
        assertSame(CountlangType.integer(), storedSubTypes.get(0));
        assertSame(CountlangType.bool(), storedSubTypes.get(1));
        assertEquals("tuple<int, bool>", firstInstance.toString());
    }

    @Test
    public void testTupleOfThreeRemainsUnieque() {
        TupleType intFrac = CountlangType.tupleOf(Arrays.asList(CountlangType.integer(), CountlangType.fraction()));
        TupleType boolInt = CountlangType.tupleOf(Arrays.asList(CountlangType.bool(), CountlangType.integer()));
        TupleType first = CountlangType.tupleOf(Arrays.asList(CountlangType.bool(), CountlangType.integer(), CountlangType.fraction()));
        TupleType second = CountlangType.tupleOf(Arrays.asList(boolInt, CountlangType.fraction()));
        TupleType third = CountlangType.tupleOf(Arrays.asList(CountlangType.bool(), intFrac));
        TupleType fourth = CountlangType.tupleOf(Arrays.asList(first));
        assertSame(first, second);
        assertSame(first, third);
        assertSame(first, fourth);
        assertEquals("tuple<bool, int, fraction>", first.toString());
    }
}
