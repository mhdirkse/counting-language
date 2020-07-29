package com.github.mhdirkse.countlang.execution;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnTypeCheck.Callback;

@RunWith(EasyMockRunner.class)
public class FunctionAndReturnTypeCheckTest {
    @Mock(MockType.STRICT)
    private Callback callback;

    private FunctionAndReturnTypeCheck instance;
    
    @Before
    public void setUp() {
        instance = new FunctionAndReturnTypeCheck(callback);
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
        instance.onReturn(1, 1, CountlangType.BOOL);
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(1, instance.getNumReturnValues());
        Assert.assertEquals(Arrays.asList(CountlangType.BOOL), instance.getReturnValues());
        verify(callback);
    }

    @Test
    public void whenEmptyReturnThenNoReturnValues() {
        replay(callback);
        instance.onReturn(1, 1);
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(0, instance.getNumReturnValues());
        Assert.assertTrue(instance.getReturnValues().isEmpty());
        verify(callback);
    }

    @Test
    public void whenStatementAfterReturnThenStatementNoEffectReported() {
        callback.reportStatementHasNoEffect(2, 3);
        replay(callback);
        instance.onReturn(1, 1);
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
        instance.onReturn(1, 1);
        instance.onReturn(2, 3);
        Assert.assertEquals(0, instance.getNestedFunctionDepth());
        Assert.assertEquals(0, instance.getNumReturnValues());
        Assert.assertTrue(instance.getReturnValues().isEmpty());
        verify(callback);        
    }

    @Test
    public void whenFunctionDefinedAndLeftThenRootReturnValueUnchanged() {
        replay(callback);
        instance.onFunctionEntered();
        instance.onReturn(1, 1, CountlangType.BOOL, CountlangType.INT);
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
        instance.onReturn(1, 1, CountlangType.BOOL);
        Assert.assertEquals(1, instance.getNestedFunctionDepth());
        Assert.assertEquals(1, instance.getNumReturnValues());
        Assert.assertEquals(Arrays.asList(CountlangType.BOOL), instance.getReturnValues());
        instance.onFunctionLeft();
        instance.onReturn(2, 3, CountlangType.INT, CountlangType.BOOL);
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
        instance.onReturn(1, 1, CountlangType.BOOL);
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onReturn(2, 3, CountlangType.INT);
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
        instance.onReturn(1, 1, CountlangType.INT, CountlangType.BOOL);
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onReturn(2, 3, CountlangType.INT);
        instance.onBranchClosed();        
        instance.onSwitchClosed();
        verify(callback);
    }
}
