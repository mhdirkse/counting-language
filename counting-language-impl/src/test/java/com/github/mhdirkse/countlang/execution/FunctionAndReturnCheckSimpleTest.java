/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        instance.onFunctionEntered("name", false);
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
        instance.onFunctionEntered("name", false);
        Assert.assertFalse(instance.isStop());
        instance.onReturn(2, 3, new ArrayList<>());
        Assert.assertFalse(instance.isStop());
        instance.setStop();
        Assert.assertTrue(instance.isStop());
        instance.onFunctionLeft();
        Assert.assertFalse(instance.isStop());
    }
}
