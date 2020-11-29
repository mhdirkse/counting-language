/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
