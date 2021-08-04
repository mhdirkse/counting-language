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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.mhdirkse.countlang.algorithm.OutputStrategy;
import com.github.mhdirkse.countlang.algorithm.TestOutputStrategy;

@RunWith(Parameterized.class)
public class IntegrationUnhappyTest implements OutputStrategy
{
    @Parameters(name = "{0}, expect errors {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {

            // Calculating
            
            {"print 2 / 0", "Division by zero"},
            {"print distribution 1 total 0", "The scored items in the distribution make count"},
            {"print distribution 1 unknown -1", "The unknown count in a distribution cannot be negative"},

            // Syntax
            
            {"print 5 +", ""}, // Syntax error.
            {"xyz", ""}, // Syntax error.
            {"print 5 ** 3", ""}, // Unknown token.
            
            // Variable references and type checking
            
            {"print x", ""}, // Undefined reference
            {"print true and 5", "Type mismatch using operator"},
            {"print 5 + true", "Type mismatch using operator"},
            {"print 3 == true", "Type mismatch using operator"},
            {"print 3 + distribution<int>", "Type mismatch using operator"},
            {"x = 3", "is not used"},
            {"x = 3; print x; x = 5;", "is not used"},
            {"function fun(int x) {return x}; print fun(true)", "Type mismatch calling function"},
            {"x = true; print x; x = 5; print x;", "Cannot change type of variable"},
            {"print distribution total false", "The amount or unknown clause of a distribution should be int"},
            {"print distribution unknown false", "The amount or unknown clause of a distribution should be int"},
            {"print distribution 1, true", "Element number 2"},
            {"print distribution true of 2 total 3", "Element number 1, the count, in distribution is bool, should be int"},
            {"print distribution 1 total true", "The amount or unknown clause of a distribution should be int."},
            {"print distribution 1 unknown true", "The amount or unknown clause of a distribution should be int."},
            {"print distribution -1 of 1;", "Item is added to distribution with negative count"},
            {"print distribution 2 of 3 total 1", "The scored items in the distribution make count 2, which is more than 1"},
            {"print distribution;", "Distribution should define its subtype"},
            {"print distribution<int> true", "Element number"},
            {"print distribution 1, true;", "Element number"},
            {"print distribution<bool> total 2", "Ambiguous"},

            // Functions
            
            {"function fun(int x, int y) {return x + y}; print fun(5)", "Argument count mismatch"},
            {"print fun(5);", "does not exist"},
            {"function fun() {return 3}; function fun() {return 5}", "was already defined"},
            {"return 3", "Return statement outside function"},
            {"function fun() {return 3; return 5}; print fun()", "Statement in function"},
            {"function fun() {return 3; print 5}", "Statement in function"},
            {"function fun(bool b) {if(b) {return 3;} else {return 5;}; return 8}", "Statement in function"},
            {"function fun(int x) {return 3;\nprint 5;}", "Statement in function"},
            {"function fun() {return 3; print 5}; print fun()", "has no effect"},
            {"function fun() {print 3}; print fun()", "does not return a value"},
            {"function fun() {function fun2() {return 3}; return 5}", "Nested functions not allowed"},
            {"x = 3; return x", "Return statement outside function"},
            {"function fun(int x) {y = x}", "does not return a value"},
            {"function fun(int x) {function nested(int y) {return y}; return x}", "Nested functions not allowed"},
            {"function fun() { {return 3}; return 5}", "Statement in function"},
            {"function fun() {if(true) {return 3} else {print 5}}", "does not return"},
            {"function fun() {if(true) {return 3}}", "does not return"}, // Else counts as branch, also if omitted.
            {"print fun()", "Function fun does not exist"},
            {"function fun(bool b) {if(b) {return 3} else {return true};};", "Type of return value"},
            {"experiment exp(bool b) {if(b) {return 3} else {return true};};", "Type of return value"},
            {"function fun(int x, bool x) {return 3;}", "Cannot reuse parameter name"},

            // Compound
            
            {"{x = 3; markUsed x}; print x", "Undefined"}, // When variable does not exist, it becomes local and is lost
            
            // if and while
            
            {"if(1) {print 3}", "must be BOOL"},
            {"while(1) {print 3}", "Test expression of while statement must be BOOL"},

            // experiments and sampling

            {"sample x from distribution 1, 2; print x", "Sampling is only allowed within an experiment."},
            {"experiment exp() {sample x from 3; return x}; print exp();", "The value you sample from is a int"},
            {"experiment exp() {sample x from distribution<int>; return x}; print exp();", "Cannot sample from empty distribution"},

            // Member functions

            // Function is a member function of distribution. x is not a distribution, and
            // there is no function with search key ("known", int).
            {"x = 1; print x.known()", "does not exist"},
            {"print (distribution 1).known(1)", "Argument count mismatch"},
            {"x = 1; print x.countOf(3)", "does not exist"},
            {"print (distribution 1).countOf()", "Argument count mismatch"},
            {"print (distribution true).countOf(1)", "Type mismatch calling function"}
        });
    }

    @Parameter(0)
    public String input;

    @Parameter(1)
    public String expectedError;

    TestOutputStrategy outputStrategy;

    @Before
    public void setUp() {
        StatusCode.setTestMode(true);
        outputStrategy = new TestOutputStrategy();
    }

    @After
    public void tearDown() {
        StatusCode.setTestMode(false);        
    }

    @Override
    public void output(final String output) {
        outputStrategy.output(output);
    }

    @Override
    public void error(final String error) {
    	outputStrategy.error(error);
    }

    private void compileAndRun(final String programText) {
    	try {
    		compileAndRunUnchecked(programText);
    	} catch(IOException e) {
    		throw new IllegalStateException(e);
    	}
    }

    private void compileAndRunUnchecked(final String programText) throws IOException {
    	StringReader reader = new StringReader(programText);
    	try {
	    	new ProgramExecutor(reader).run(this);
    	}
    	finally {
    		reader.close();
    	}
    }

    @Test
    public void test() {
        compileAndRun(input);
        Assert.assertTrue(outputStrategy.getNumErrors() >= 1);
        Assert.assertThat(getErrors(), CoreMatchers.containsString(expectedError));
    }

    private String getErrors() {
        List<String> lines = new ArrayList<>();
        for(int i = 0; i < outputStrategy.getNumErrors(); i++) {
            lines.add(outputStrategy.getError(i));
        }
        return lines.stream().collect(Collectors.joining(". "));
    }
}
