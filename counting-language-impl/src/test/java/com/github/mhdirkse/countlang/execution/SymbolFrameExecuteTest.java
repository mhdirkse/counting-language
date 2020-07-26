package com.github.mhdirkse.countlang.execution;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SymbolFrameExecuteTest {
    private static final StackFrameAccess DUMMY_ACCESS = StackFrameAccess.SHOW_PARENT;
    private static final String SYMBOL = "x";
    private static final Integer VALUE = Integer.valueOf(10);

    private SymbolFrameExecute instance;

    @Before
    public void setUp() {
        instance = new SymbolFrameExecute(DUMMY_ACCESS);
    }

    @Test
    public void testWhenSymbolWrittenThenSymbolCanBeRead() {
        instance.write(SYMBOL, VALUE, 1, 1);
        Object result = instance.read(SYMBOL, 2, 3);
        Assert.assertEquals(VALUE, result);
    }
}
