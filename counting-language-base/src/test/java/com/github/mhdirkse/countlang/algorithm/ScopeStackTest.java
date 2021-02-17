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
        instance.findScope("someName");
    }

    @Test
    public void whenMultipleScopesHaveSymbolThenTopmostChosen() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        ScopeImpl top = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findScope("someName");
        assertSame(top, actual);
    }

    @Test
    public void whenTopScopeDoesNotHaveSymbolThenScopeHavingSymbolReturned() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        ScopeImpl top = new ScopeImpl(ScopeAccess.SHOW_PARENT);
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findScope("someName");
        assertSame(bottom, actual);        
    }

    @Test
    public void whenNoScopeHasSymbolThenTopReturned() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT);
        ScopeImpl top = new ScopeImpl(ScopeAccess.SHOW_PARENT);
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findScope("someName");
        assertSame(top, actual);        
    }

    @Test
    public void whenScopeHidesParentThenScopesBelowAreIgnored() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        ScopeImpl top = new ScopeImpl(ScopeAccess.HIDE_PARENT);
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findScope("someName");
        assertSame(top, actual);                
    }
}
