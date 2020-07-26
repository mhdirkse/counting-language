package com.github.mhdirkse.countlang.execution;

import static com.github.mhdirkse.countlang.execution.StackFrameAccess.HIDE_PARENT;
import static com.github.mhdirkse.countlang.execution.StackFrameAccess.SHOW_PARENT;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.mhdirkse.countlang.utils.Stack;

import org.junit.Assert;

@RunWith(Parameterized.class)
public class SymbolFrameStackTest {
    private static SymbolFrameStub item(final StackFrameAccess access, final boolean hasSymbol) {
        return new SymbolFrameStub(access, hasSymbol);
    }

    private static TestableSymbolFrameStack instance(SymbolFrameStub... items) {
        Stack<SymbolFrameStub> arg = new Stack<>();
        for(int i = 0; i < items.length; i++) {
            items[i].setSeq(i);
            arg.push(items[i]);
        }
        return new TestableSymbolFrameStack(arg); 
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"If in frame, give frame", instance(item(SHOW_PARENT, true)), 0},
            {"If not found, top frame", instance(item(SHOW_PARENT, false)), 0},
            {"Choose top frame", instance(item(SHOW_PARENT, true), item(SHOW_PARENT, true)), 1},
            {"If not in top, take next", instance(item(SHOW_PARENT, true), item(SHOW_PARENT, false)), 0},
            {"If hide parent and found on top, still top", instance(item(SHOW_PARENT, true), item(HIDE_PARENT, true)), 1},
            {"If hid parent, dont go down", instance(item(SHOW_PARENT, true), item(HIDE_PARENT, false)), 1}
        });
    }
    
    @Parameter
    public String description;
    
    @Parameter(1)
    public TestableSymbolFrameStack instance;

    @Parameter(2)
    public int expectedSeq;

    @Test
    public void test() {
        SymbolFrameStub result = (SymbolFrameStub) instance.findFrame(SymbolFrameStub.SYMBOL);
        Assert.assertEquals(expectedSeq, result.getSeq());
    }
}
