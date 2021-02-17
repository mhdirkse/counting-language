package com.github.mhdirkse.countlang.algorithm;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

public class CountlangStackTest {
    private CountlangStack<CountlangStackItemImpl> instance;

    @Before
    public void setUp() {
        instance = new CountlangStack<CountlangStackItemImpl>();
    }

    @Test(expected = IllegalStateException.class)
    public void whenEmptyStackQueriedThenError() {
        instance.findFrame("someName");
    }

    @Test
    public void whenMultipleItemsHaveSymbolThenTopmostChosen() {
        CountlangStackItemImpl bottom = new CountlangStackItemImpl(StackFrameAccess.SHOW_PARENT, "someName");
        CountlangStackItemImpl top = new CountlangStackItemImpl(StackFrameAccess.SHOW_PARENT, "someName");
        instance.push(bottom);
        instance.push(top);
        CountlangStackItemImpl actual = instance.findFrame("someName");
        assertSame(top, actual);
    }

    @Test
    public void whenTopFrameDoesNotHaveSymbolThenFrameHavingSymbolReturned() {
        CountlangStackItemImpl bottom = new CountlangStackItemImpl(StackFrameAccess.SHOW_PARENT, "someName");
        CountlangStackItemImpl top = new CountlangStackItemImpl(StackFrameAccess.SHOW_PARENT);
        instance.push(bottom);
        instance.push(top);
        CountlangStackItemImpl actual = instance.findFrame("someName");
        assertSame(bottom, actual);        
    }

    @Test
    public void whenNoFrameHasSymbolThenTopReturned() {
        CountlangStackItemImpl bottom = new CountlangStackItemImpl(StackFrameAccess.SHOW_PARENT);
        CountlangStackItemImpl top = new CountlangStackItemImpl(StackFrameAccess.SHOW_PARENT);
        instance.push(bottom);
        instance.push(top);
        CountlangStackItemImpl actual = instance.findFrame("someName");
        assertSame(top, actual);        
    }

    @Test
    public void whenFrameHidesParentThenFramesBelowAreIgnored() {
        CountlangStackItemImpl bottom = new CountlangStackItemImpl(StackFrameAccess.SHOW_PARENT, "someName");
        CountlangStackItemImpl top = new CountlangStackItemImpl(StackFrameAccess.HIDE_PARENT);
        instance.push(bottom);
        instance.push(top);
        CountlangStackItemImpl actual = instance.findFrame("someName");
        assertSame(top, actual);                
    }
}
