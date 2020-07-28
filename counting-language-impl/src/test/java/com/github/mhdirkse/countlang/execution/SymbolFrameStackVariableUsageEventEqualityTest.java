package com.github.mhdirkse.countlang.execution;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.mhdirkse.countlang.execution.SymbolFrameStackVariableUsage.Event;

import org.junit.Assert;

@RunWith(Parameterized.class)
public class SymbolFrameStackVariableUsageEventEqualityTest {
    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        Event ev = new Event("name", 2, 3);
        Event eqEv = new Event("name", 2, 3);
        return Arrays.asList(new Object[][] {
            {"This check", ev, ev, true},
            {"Null check", ev, null, false},
            {"Different class check", ev, 0, false},
            {"Equivalent", ev, eqEv, true},
            {"Other line", ev, new Event("name", 10, 3), false},
            {"Other column", ev, new Event("name", 2, 10), false},
            {"Other name", ev, new Event("otherName", 2, 3), false}
        });
    }

    @Parameter(0)
    public String description;

    @Parameter(1)
    public Event instance;

    @Parameter(2)
    public Object other;

    @Parameter(3)
    public boolean expectedEqualsResult;

    @Test
    public void testEquality() {
        Assert.assertEquals(expectedEqualsResult, instance.equals(other));
    }

    @Test
    public void testHashCode() {
        if(other != null) {
            Assert.assertEquals(expectedEqualsResult, instance.hashCode() == other.hashCode());
        }
    }
}
