package com.github.mhdirkse.countlang.execution;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck.TypeCheckContext;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnTypeCheck.Callback;

@RunWith(EasyMockRunner.class)
public class FunctionAndReturnTypeCheckTest {
    @Mock(MockType.STRICT)
    private Callback callback;

    private FunctionAndReturnTypeCheck instance;
    
    @Before
    public void setUp() {
        instance = new FunctionAndReturnTypeCheck(callback, TypeCheckContext::new);
    }

    @Test
    public void whenNoReturnThenNoReturnValues() {
        replay(callback);
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(0, instance.getNumReturnValues());
        Assert.assertTrue(instance.getReturnValues().isEmpty());
        verify(callback);
    }

    @Test
    public void whenReturnOneValueThenValueReturned() {
        replay(callback);
        instance.onReturn(1, 1, Arrays.asList(CountlangType.BOOL));
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(1, instance.getNumReturnValues());
        Assert.assertEquals(Arrays.asList(CountlangType.BOOL), instance.getReturnValues());
        verify(callback);
    }

    @Test
    public void whenEmptyReturnThenNoReturnValues() {
        replay(callback);
        instance.onReturn(1, 1, new ArrayList<>());
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(0, instance.getNumReturnValues());
        Assert.assertTrue(instance.getReturnValues().isEmpty());
        verify(callback);
    }

    @Test
    public void whenStatementAfterReturnThenStatementNoEffectReported() {
        callback.reportStatementHasNoEffect(2, 3);
        replay(callback);
        instance.onReturn(1, 1, new ArrayList<>());
        instance.onStatement(2, 3);
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(0, instance.getNumReturnValues());
        Assert.assertTrue(instance.getReturnValues().isEmpty());
        verify(callback);
    }

    @Test
    public void whenExtraReturnAfterReturnThenStatementNoEffectReported() {
        callback.reportStatementHasNoEffect(2, 3);
        replay(callback);
        instance.onReturn(1, 1, new ArrayList<>());
        instance.onReturn(2, 3, new ArrayList<>());
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(0, instance.getNumReturnValues());
        Assert.assertTrue(instance.getReturnValues().isEmpty());
        verify(callback);        
    }

    @Test
    public void whenFunctionDefinedAndLeftThenRootReturnValueUnchanged() {
        replay(callback);
        instance.onFunctionEntered();
        instance.onReturn(1, 1, Arrays.asList(CountlangType.BOOL, CountlangType.INT));
        Assert.assertEquals(1, instance.getNestedFunctionDepth());
        Assert.assertEquals(2, instance.getNumReturnValues());
        Assert.assertEquals(Arrays.asList(CountlangType.BOOL, CountlangType.INT), instance.getReturnValues());
        instance.onFunctionLeft();
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(0, instance.getNumReturnValues());
        Assert.assertTrue(instance.getReturnValues().isEmpty());
        verify(callback);
    }

    @Test
    public void whenFunctionThenOuterScopeUnaffected() {
        replay(callback);
        instance.onFunctionEntered();
        instance.onReturn(1, 1, Arrays.asList(CountlangType.BOOL));
        Assert.assertEquals(1, instance.getNestedFunctionDepth());
        Assert.assertEquals(1, instance.getNumReturnValues());
        Assert.assertEquals(Arrays.asList(CountlangType.BOOL), instance.getReturnValues());
        instance.onFunctionLeft();
        instance.onReturn(2, 3, Arrays.asList(CountlangType.INT, CountlangType.BOOL));
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(2, instance.getNumReturnValues());
        Assert.assertEquals(Arrays.asList(CountlangType.INT, CountlangType.BOOL), instance.getReturnValues());
        verify(callback);
    }

    @Test
    public void whenInconsistentReturnTypeThenReported() {
        callback.reportInconsistentReturnType(1, 1, 2, 3);
        replay(callback);
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onReturn(1, 1, Arrays.asList(CountlangType.BOOL));
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onReturn(2, 3, Arrays.asList(CountlangType.INT));
        instance.onBranchClosed();        
        instance.onSwitchClosed();
        verify(callback);
    }

    @Test
    public void whenInconsistentReturnCountThenReported() {
        callback.reportInconsistentReturnType(1, 1, 2, 3);
        replay(callback);
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onReturn(1, 1, Arrays.asList(CountlangType.INT, CountlangType.BOOL));
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onReturn(2, 3, Arrays.asList(CountlangType.INT));
        instance.onBranchClosed();        
        instance.onSwitchClosed();
        verify(callback);
    }

    @Test
    public void testContextProperties() {
        replay(callback);
        Assert.assertFalse(instance.hasExplicitReturn());
        Assert.assertFalse(instance.isStop());
        instance.onFunctionEntered();
        Assert.assertFalse(instance.hasExplicitReturn());
        Assert.assertFalse(instance.isStop());
        instance.onReturn(2, 3, new ArrayList<>());
        Assert.assertTrue(instance.hasExplicitReturn());
        Assert.assertEquals(2, instance.getLineFirstReturn());
        Assert.assertEquals(3, instance.getColumnFirstReturn());
        Assert.assertFalse(instance.isStop());
        instance.setStop();
        Assert.assertTrue(instance.isStop());
        instance.onFunctionLeft();
        Assert.assertFalse(instance.hasExplicitReturn());
        Assert.assertFalse(instance.isStop());
        instance.onReturn(10, 20, Arrays.asList(CountlangType.BOOL));
        Assert.assertTrue(instance.hasExplicitReturn());
        Assert.assertEquals(10, instance.getLineFirstReturn());
        Assert.assertEquals(20, instance.getColumnFirstReturn());
        verify(callback);
    }
}
