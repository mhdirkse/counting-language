package com.github.mhdirkse.countlang.execution;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ValueStackTest {
    private ValueStack instance;

    @Before
    public void setUp() {
        instance = new ValueStack();
    }

    @Test
    public void testLastInFirstOut() {
        instance.push(Integer.valueOf(1));
        instance.push(Integer.valueOf(2));
        int popped = ((Integer) instance.pop()).intValue();
        Assert.assertEquals(2, popped);
        popped = ((Integer) instance.pop()).intValue();
        Assert.assertEquals(1, popped);
    }

    @Test
    public void testEmpty() {
        Assert.assertTrue(instance.isEmpty());
        instance.push(Integer.valueOf(1));
        Assert.assertFalse(instance.isEmpty());
    }
}
