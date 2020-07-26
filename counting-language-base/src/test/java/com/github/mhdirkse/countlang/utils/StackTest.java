package com.github.mhdirkse.countlang.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StackTest {
    private Stack<Integer> instance;

    @Before
    public void setUp() {
        instance = new Stack<Integer>();
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

    @Test
    public void testPushAll() {
        List<Integer> items = Arrays.asList(1, 2);
        Stack<Integer> instance = new Stack<>();
        instance.pushAll(items);
        Assert.assertEquals(2, instance.pop().intValue());
        Assert.assertEquals(1, instance.pop().intValue());
        Assert.assertTrue(instance.isEmpty());
    }

    @Test
    public void testRepeatedPop() {
        instance.push(Integer.valueOf(1));
        instance.push(Integer.valueOf(2));
        List<Integer> result = instance.repeatedPop(2);
        Assert.assertEquals(Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)), result);
    }

    @Test
    public void testTopToBottomIterator() {
        instance.push(Integer.valueOf(1));
        instance.push(Integer.valueOf(2));
        Iterator<Integer> it = instance.topToBottomIterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(2, it.next().intValue());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1, it.next().intValue());
        Assert.assertFalse(it.hasNext());
    }
}
