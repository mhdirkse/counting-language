package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck.SimpleContext;

public class FunctionAndReturnCheckSimpleTest {  
    private FunctionAndReturnCheckSimple<CountlangType, SimpleContext<CountlangType>> instance;

    @Before
    public void setUp() {
        instance = new FunctionAndReturnCheckSimple<CountlangType, SimpleContext<CountlangType>>(
                SimpleContext::new);
    }

    @Test
    public void whenNoReturnThenNoReturnValues() {
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(0, instance.getNumReturnValues());
        Assert.assertTrue(instance.getReturnValues().isEmpty());
    }

    @Test
    public void whenReturnOneValueThenValueReturned() {
        instance.onReturn(1, 1, Arrays.asList(CountlangType.BOOL));
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(1, instance.getNumReturnValues());
        Assert.assertEquals(Arrays.asList(CountlangType.BOOL), instance.getReturnValues());
    }

    @Test
    public void whenEmptyReturnThenNoReturnValues() {
        instance.onReturn(1, 1, new ArrayList<>());
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(0, instance.getNumReturnValues());
        Assert.assertTrue(instance.getReturnValues().isEmpty());
    }

    @Test
    public void whenFunctionDefinedAndLeftThenRootReturnValueUnchanged() {
        instance.onFunctionEntered();
        instance.onReturn(1, 1, Arrays.asList(CountlangType.BOOL, CountlangType.INT));
        Assert.assertEquals(1, instance.getNestedFunctionDepth());
        Assert.assertEquals(2, instance.getNumReturnValues());
        Assert.assertEquals(Arrays.asList(CountlangType.BOOL, CountlangType.INT), instance.getReturnValues());
        instance.onFunctionLeft();
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(0, instance.getNumReturnValues());
        Assert.assertTrue(instance.getReturnValues().isEmpty());
    }

    @Test
    public void testContextProperties() {
        Assert.assertFalse(instance.isStop());
        instance.onFunctionEntered();
        Assert.assertFalse(instance.isStop());
        instance.onReturn(2, 3, new ArrayList<>());
        Assert.assertFalse(instance.isStop());
        instance.setStop();
        Assert.assertTrue(instance.isStop());
        instance.onFunctionLeft();
        Assert.assertFalse(instance.isStop());
    }
}
