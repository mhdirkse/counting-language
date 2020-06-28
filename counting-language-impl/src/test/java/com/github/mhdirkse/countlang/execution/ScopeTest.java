package com.github.mhdirkse.countlang.execution;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.StackFrameAccess;

public class ScopeTest {
    private Scope instance;

    @Before
    public void setUp() {
        instance = new Scope();
    }

    @Test
    public void whenNoStackFramesPushedThenScopeUsable() {
        Assert.assertFalse(instance.hasSymbol("x"));
        instance.putSymbol("x", CountlangType.INT.getExample());
        Assert.assertTrue(instance.hasSymbol("x"));
        Assert.assertEquals(CountlangType.INT, instance.getCountlangType("x"));
        Assert.assertEquals(CountlangType.INT.getExample(), instance.getValue("x"));
    }

    @Test
    public void whenStackFrameHidingParentPushedThenParentHidden() {
        instance.putSymbol("x", CountlangType.INT.getExample());
        instance.pushFrame(new StackFrame(StackFrameAccess.HIDE_PARENT));
        Assert.assertFalse(instance.hasSymbol("x"));
        instance.putSymbol("x", CountlangType.BOOL.getExample());
        Assert.assertTrue(instance.hasSymbol("x"));
        Assert.assertEquals(CountlangType.BOOL, instance.getCountlangType("x"));
        Assert.assertEquals(CountlangType.BOOL.getExample(), instance.getValue("x"));
        instance.popFrame();
        Assert.assertTrue(instance.hasSymbol("x"));
        Assert.assertEquals(CountlangType.INT, instance.getCountlangType("x"));
        Assert.assertEquals(CountlangType.INT.getExample(), instance.getValue("x"));
    }

    @Test
    public void whenStackFrameShowingParentPushedThenParentAccessed() {
        instance.putSymbol("x", CountlangType.INT.getExample());
        instance.pushFrame(new StackFrame(StackFrameAccess.SHOW_PARENT));
        Assert.assertTrue(instance.hasSymbol("x"));
        Assert.assertEquals(CountlangType.INT, instance.getCountlangType("x"));
        Assert.assertEquals(CountlangType.INT.getExample(), instance.getValue("x"));
        instance.putSymbol("x", CountlangType.BOOL.getExample());
        instance.popFrame();
        Assert.assertTrue(instance.hasSymbol("x"));
        Assert.assertEquals(CountlangType.BOOL, instance.getCountlangType("x"));
        Assert.assertEquals(CountlangType.BOOL.getExample(), instance.getValue("x"));
    }
}
