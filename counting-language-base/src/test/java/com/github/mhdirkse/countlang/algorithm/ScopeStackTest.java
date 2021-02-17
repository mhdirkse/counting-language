package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

public class ScopeStackTest {
    private ScopeStack<ScopeImpl> instance;

    @Before
    public void setUp() {
        instance = new ScopeStack<ScopeImpl>();
    }

    @Test(expected = IllegalStateException.class)
    public void whenEmptyStackQueriedThenError() {
        instance.findFrame("someName");
    }

    @Test
    public void whenMultipleItemsHaveSymbolThenTopmostChosen() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        ScopeImpl top = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findFrame("someName");
        assertSame(top, actual);
    }

    @Test
    public void whenTopFrameDoesNotHaveSymbolThenFrameHavingSymbolReturned() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        ScopeImpl top = new ScopeImpl(ScopeAccess.SHOW_PARENT);
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findFrame("someName");
        assertSame(bottom, actual);        
    }

    @Test
    public void whenNoFrameHasSymbolThenTopReturned() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT);
        ScopeImpl top = new ScopeImpl(ScopeAccess.SHOW_PARENT);
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findFrame("someName");
        assertSame(top, actual);        
    }

    @Test
    public void whenFrameHidesParentThenFramesBelowAreIgnored() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        ScopeImpl top = new ScopeImpl(ScopeAccess.HIDE_PARENT);
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findFrame("someName");
        assertSame(top, actual);                
    }
}
