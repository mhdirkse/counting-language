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

package com.github.mhdirkse.countlang.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import org.junit.Assert;

@RunWith(Parameterized.class)
public class IntegrationHappyMultipleOutputTest extends IntegrationHappyTestBase {
    private static Object[][] rawData() {
        return new Object[][] {
            // Test that function call expression does not stop statement group
            {"function fun() {print 3; return 0}; x = fun(); print x", Arrays.asList("3", "0")}
        };
    }

    @Parameters(name = "{0}, expect output {2}")
    public static Collection<Object[]> data() {
        List<Object[]> result = new ArrayList<>();
        for(Object[] raw: rawData() ) {
            @SuppressWarnings("unchecked")
            List<String> rawExpected = (List<String>) raw[1];
            String expectedOutputString = rawExpected.stream().collect(Collectors.joining(", "));
            result.add(new Object[] {raw[0], raw[1], expectedOutputString});
        }
        return result;
    }

    @Parameter(0)
    public String input;

    @Parameter(1)
    public List<String> expectedOutput;

    @Parameter(2)
    public String expectedOutputAsString;

    @Test
    public void testMultipleOutputs() {
        compileAndRun(input);
        Assert.assertEquals(0, outputStrategy.getNumErrors());
        Assert.assertEquals(expectedOutput.size(), outputStrategy.getNumLines());
        for(int i = 0; i < expectedOutput.size(); i++) {
            Assert.assertEquals(expectedOutput.get(i), outputStrategy.getLine(i));
        }
    }
}
