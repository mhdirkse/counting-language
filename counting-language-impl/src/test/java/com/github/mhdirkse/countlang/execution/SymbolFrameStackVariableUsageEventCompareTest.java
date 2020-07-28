package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.mhdirkse.countlang.execution.SymbolFrameStackVariableUsage.Event;

@RunWith(Parameterized.class)
public class SymbolFrameStackVariableUsageEventCompareTest {
    @Parameters(name = "{0} and {1}")
    public static Collection<Object[]> data() {
        List<Event> correctlySorted = Arrays.asList(
                new Event("name1", 2, 2),
                new Event("name2", 2, 2),
                new Event("name1", 2, 3),
                new Event("name1", 3, 2));
        List<Object[]> result = new ArrayList<>();
        for(int i = 0; i < (correctlySorted.size() - 1); i++) {
            for(int j = i+1; j < correctlySorted.size(); j++) {
                result.add(new Object[] {correctlySorted.get(i), correctlySorted.get(j)});
            }
        }
        return result;
    }

    @Parameter(0)
    public Event smallest;
    
    @Parameter(1)
    public Event greatest;

    @Test
    public void testSmallestCompareToGreatestIsNeg() {
        Assert.assertEquals(-1, smallest.compareTo(greatest));
    }

    @Test
    public void testGreatestCompareToSmallestIsPos() {
        Assert.assertEquals(1, greatest.compareTo(smallest));
    }

    @Test
    public void testEquality() {
        Assert.assertEquals(0, smallest.compareTo(smallest));
    }
}
