package com.github.mhdirkse.countlang.tasks;

import static com.github.mhdirkse.countlang.tasks.Constants.DECREMENT_OF_MIN_INT_PLUS_ONE;
import static com.github.mhdirkse.countlang.tasks.Constants.INCREMENT_OF_MAX_INT_MINUS_ONE;
import static com.github.mhdirkse.countlang.tasks.Constants.MAX_INT;
import static com.github.mhdirkse.countlang.tasks.Constants.MIN_INT;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.TestOutputStrategy;

@RunWith(Parameterized.class)
public class IntegrationHappyTest implements OutputStrategy
{
    @Parameters(name = "{0}, expect errors {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"print 5 + 3", "8"},
            {"print 5 - 3", "2"},
            {"print 5 * 3", "15"},
            {"print 15 / 3", "5"},
            {"print 5 / 3", "1"}, // Round down.
            {"print -5 / 3", "-2"}, // Round down.
            {"print -5 / -3", "1"}, // Round down.
            {"print 5 / -3", "-2"}, // Round down.
            {"print 0 / 3", "0"}, // Do not round unnecessarily.
            {"print 0 / -3", "0"}, // Do not round unnecessarily.
            {"print -5--3", "-2"},
            {INCREMENT_OF_MAX_INT_MINUS_ONE, MAX_INT}, // No overflow.
            {DECREMENT_OF_MIN_INT_PLUS_ONE, MIN_INT}, // No underflow.
            {"print testFunction(4)", "9"},
            {"print 2 + testFunction(4)", "11"},
            {"print testFunction(testFunction(4))", "14"},
            {"function myFun(x, y) {z = x - y; return z}; print myFun(5, 3)", "2"},
            {"function myFun(x) {return x}; print myFun(2 + 3)", "5"},
            {"function myFun() {return 10}; print myFun()", "10"}, // Function without arguments.
            {"x = 5; print x;", "5"}, // Allow extra ; outside functions.
            {"function fun() {return 5;}; print 5", "5"}, // Allow extra ; within function.
            {"x = 5; print -x", "-5"}, // The unary minus.
            {"x = 5; y = 3; print x - - y", "8"}, // Combine unary minus with ordinary minus.
            {"x = 5; y = 3; print x*-y", "-15"}, // Combine unary minus with multiplication.
            {"x = 5; y = 3; print -x-y", "-8"} // Have unary minus as first child of binary operator.
        });
    }

    @Parameter(0)
    public String input;

    @Parameter(1)
    public String expectedResult;

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
	    	new ExecuteProgramTask(reader).run(this);
    	}
    	finally {
    		reader.close();
    	}
    }

    @Test
    public void test() {
        compileAndRun(input);
        Assert.assertEquals(0, outputStrategy.getNumErrors());
        Assert.assertEquals(expectedResult, outputStrategy.getLine(0));
    }
}
