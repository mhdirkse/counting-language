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
            
            {"print 2 div 0", "Division by zero"},
            {"print 2 / 0", "Division by zero"},
            {"print (2 / 1) / 0", "Division by zero"},
            {"print distribution 1 total 0", "The scored items in the distribution make count"},
            {"print distribution 1 unknown -1", "The unknown count in a distribution cannot be negative"},
            // No implicit promotion to fraction with relational operators
            {"print (3 / 1) <= 3", "Type mismatch using operator"},
            {"print (3 / 1) == 3", "Type mismatch using operator"},
            {"print true + false", "Type mismatch using operator"},
            // Type checks
            {"print (3 / 1) div 2", "Type mismatch using operator"},
            {"print (3 / 1) div (2 / 1)", "Type mismatch using operator"},
            {"print true / true", "Type mismatch using operator"},
            {"print not 1", "Type mismatch using operator"},
            {"print 1 and 2", "Type mismatch using operator"},
            {"print false <= true", "Type mismatch using operator"},

            // Arrays

            {"x = [2, 5, 6]; print x[0]", "Invalid array index"},
            {"x = [2, 5, 6]; print x[4]", "Array index"},
            {"x = [2, 5, 6]; print x[true]", "An array index should be integer"},
            {"x = 5; print x[1]", "Cannot get an element from something that is not an array"},
            {"x = [1, true]", "has invalid type"},

            // Tuples

            {"print distribution<tuple<tuple<int, int>, bool>>", "Tuple types are always flat"},
            {"print tuple<tuple<int, int>, bool>[]", "Tuple types are always flat"},
            {"function fun(tuple<tuple<fraction, int>, bool> v) {return 3}; print fun(tuple 3/5, 3, true)", "Tuple types are always flat"},
            {"print tuple 1", "A tuple has at least two members"},
            {"print distribution<tuple<int>>", "A tuple has at least two members"},
            {"print tuple<int>[]", "A tuple has at least two members"},
            {"function fun(tuple<int> v) {return true}; print fun(3)", "A tuple has at least two members"},
            {"x, y = 3; print x", "When you assign multiple variables"},
            {"x, y = tuple 1, 2, 3; print x; print y", "Cannot assign 3 values to 2 variables"},
            {"x, y = tuple 3, true; print x; print y; x = true", "Cannot change type of variable"},
            {"x, y = tuple 3, true; print x; print y; y = 3", "Cannot change type of variable"},
            {"experiment exp() {sample x, y from distribution 3; return x}; print exp()", "When you assign multiple variables"},
            {"experiment exp() {sample x, y, z from distribution (tuple 1, 2); return x, y, z}; print exp()", "Cannot assign 2 values to 3 variables"},
            {"experiment exp() {sample x, y from distribution (tuple 3, true); x = true; return x}; print exp()", "Cannot change type of variable"},
            {"experiment exp() {sample x, y from distribution (tuple 3, true); y = 3; return x}; print exp()", "Cannot change type of variable"},

            {"b = true; t = tuple 3, true; b = t[1]", "Cannot change type of variable"},
            {"i = 3; t = tuple 3, true; i = t[2]", "Cannot change type of variable"},
            {"idx = 2; t = tuple 3, true; b = false; b = t[idx]; print b", "A tuple dereferencing expression must be a constant to allow for type checking"},
            {"t = tuple 1, true; x = t[3]; print x", "Tuple index out of bounds, got 3"},
            {"t = tuple 1, true; x = t[4]; print x", "Tuple index out of bounds, got 4"},
            {"t = tuple 1, true; print t[0]", "Tuple index must be at least one, got 0"},
            {"t = tuple 1, true; print t[-1]", "Tuple index must be at least one, got -1"},
            
            // Array selectors and ranges.

            {"t = tuple true, 1; t2 = t[3, 1]", "Tuple index out of bounds"},
            {"t = tuple 1, true; t2 = t[true, 1]; print t2", "An array index should be integer, but dereferencing value #1 is not"},
            {"t = tuple true, 1; t = t[1, 1]; print t", "Cannot change type of variable"},
            {"a = [1, 2, 3, 4]; print a[2, true]", "An array index should be integer, but dereferencing value #2 is not"},
            // Error is found at runtime, not analysis time
            {"a = [1, 2, 3, 4]; print a[2, 5]", "more than array size"},
            // ANTLR tries to parse this like "[1:2:(3:4)]". Therefore we cannot do better than this.
            {"print [1:2:3:4]", "Range must have step type int because the start value has that type"},
            {"x = 1:2; print x", "Variables of a range type are not allowed"},
            {"print 1 + (2:3)", "Type mismatch using operator +"},
            {"markUsed 1 + (2:3)", "Type mismatch using operator +"},
            {"markUsed 2:3", "Ranges can only be used to construct distributions or arrays, not as values by themselves."},
            {"function fun() {return 1:2}; print fun()", "Ranges can only be used to construct distributions or arrays, not as values by themselves"},
            {"print [1:2]:3", "Ranges must be formed from integers or fractions, but got"},
            {"print 3:(5/2)", "Range must have end value of type"},
            {"t = tuple 1, 2:3; print t", "Ranges can only be used to construct distributions or arrays, not as values by themselves."},
            {"t = tuple 1, true; x = 2; print t[1:x]", "A tuple dereferencing expression must be a constant to allow for type checking"},
            {"t = tuple 1, true; print t[2:1]", "Invalid step in range 2:1:1"},
            {"t = tuple true, 1; print t[0:2]", "Tuple index out of bounds, got 0"},
            {"a = [11, 12, 13, 14]; print a[3:5]", "Index out of bounds: size = 4, index = 5"},
            {"a = [11, 12, 13, 14]; print a[3:2]", "Invalid range 3:1:2"},
            {"d = distribution 2:1; print d", "Invalid range 2:1:1"},
            {"d = distribution 2 of 2:1; print d", "Invalid range 2:1:1"},

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
            {"print distribution total 1", "Distribution should define its subtype"},
            {"print distribution unknown 1", "Distribution should define its subtype"},
            {"print distribution", "Distribution should define its subtype"},

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
            {"function fun(array<int> x) {return x[1]}; print fun(true)", "Type mismatch calling function"},

            // Compound
            
            {"{x = 3; markUsed x}; print x", "Undefined"}, // When variable does not exist, it becomes local and is lost
            
            // if and while
            
            {"if(1) {print 3}", "must be BOOL"},
            {"while(1) {print 3}", "Test expression of while statement must be BOOL"},
            {"repeat(true) {print 3}", "Repetition count of repeat statement must be int, but was bool"},
            {"repeat(-1) {print 3}", "Repeat statement cannot have a negative repeat count, got -1"},
            // Test that we cannot reassign the loop variable within the repetition
            {"result = 0; for x in [3, 5] {result = result + x; x = 1}; print result", "For ... in loop variable x overwritten inside repetition"},
            // Same when loop variable already exists
            {"x = 0; result = 0; for x in [3, 5] {result = result + x; x = 1}; print result", "For ... in loop variable x overwritten inside repetition"},
            {"for x in 5 {print x}", "A for ... in statement should iterate over an array, got a int"},

            // experiments and sampling

            {"sample x from distribution 1, 2; print x", "Sampling is only allowed within an experiment."},
            {"experiment exp() {sample x from 3; return x}; print exp();", "The value you sample from is a int"},
            {"experiment exp() {sample x from distribution<int>; return x}; print exp();", "Cannot sample from empty distribution"},
            {"experiment exp() {sample x as true from distribution 1; return x}; print exp()", "When sampling multiple values from a distribution, the number to sample must be int"},
            {"experiment exp() {num = 0; sample x as num from distribution 1; return x}; print exp()", "You have to sample at least one copy from a distribution, got 0"},
            {"experiment exp() {sample x as 10*1000*1000+1 from distribution 1; return x}; print exp()", "Too many samples from distribution, got"},
            {"experiment exp() {sample x as 2 from distribution<int>; return x}; print exp()", "Cannot sample from empty distribution"},
            {"experiment exp() {sample x as 2 from 1; return x}; print exp()", "The value you sample from is a int, but should be DISTRIBUTION"},

            // Member functions

            {"print (distribution 1000000000000 of 3).ascending()", "is too big"},
            {"print (distribution<int> total 1).ascending()", "that has unknown"},
            {"print (distribution 1000000000000 of 3).descending()", "is too big"},
            {"print (distribution<int> total 1).descending()", "that has unknown"},
            
            // Function is a member function of distribution. x is not a distribution, and
            // there is no function with search key ("known", int).
            {"x = 1; print x.known()", "does not exist"},
            {"print (distribution 1).known(1)", "Argument count mismatch"},
            {"x = 1; print x.countOf(3)", "does not exist"},
            {"print (distribution 1).countOf()", "Argument count mismatch"},
            {"print (distribution true).countOf(1)", "Type mismatch calling function"},
            // Member distribution<int>.sum() requires that distribution does not have unknown
            {"print (distribution 1 total 3).sum()", "unknown"},
            {"print (distribution 1).sum(1)", "Argument count mismatch"},
            {"x = 1; print x.sum()", "does not exist"},
            {"print (distribution true).sum()", "does not exist"},
            {"print distribution<int>.E()", "Division by zero"},
            {"print distribution<fraction>.E()", "Division by zero"}
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
