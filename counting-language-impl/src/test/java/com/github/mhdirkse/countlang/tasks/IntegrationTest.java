package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.CoreMatchers;
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
public class IntegrationTest implements OutputStrategy
{
    @Parameters(name = "{0}, expect errors {1}")
    public static Collection<Object[]> data() {
        String INCREMENT_OF_MAX_INT_MINUS_ONE = String.format("print %d + %d", Integer.MAX_VALUE - 1, 1);
        String MAX_INT = String.format("%d", Integer.MAX_VALUE);
        String INCREMENT_OF_MAX_INT = String.format("print %d + %d", Integer.MAX_VALUE, 1);
        String DECREMENT_OF_MIN_INT_PLUS_ONE = String.format("print %d - %d", Integer.MIN_VALUE + 1, 1);
        String MIN_INT = String.format("%d", Integer.MIN_VALUE);
        String DECREMENT_OF_MIN_INT = String.format("print %d - %d", Integer.MIN_VALUE, 1);

        return Arrays.asList(new Object[][] {
            {"print 5 + 3", false, "8", null},
            {"print 5 - 3", false, "2", null},
            {"print 5 * 3", false, "15", null},
            {"print 15 / 3", false, "5", null},
            {"print 5 / 3", false, "1", null}, // Round down.
            {"print -5 / 3", false, "-2", null}, // Round down.
            {"print -5 / -3", false, "1", null}, // Round down.
            {"print 5 / -3", false, "-2", null}, // Round down.
            {"print 0 / 3", false, "0", null}, // Do not round unnecessarily.
            {"print 0 / -3", false, "0", null}, // Do not round unnecessarily.
            {"print -5--3", false, "-2", null},
            {"print 2 / 0", true, null, "Division by zero"},
            {"print 12345678901234567890", true, null, "Integer value is too big to store"},
            {INCREMENT_OF_MAX_INT_MINUS_ONE, false, MAX_INT, null}, // No overflow.
            {INCREMENT_OF_MAX_INT, true, null, "Overflow or underflow"},
            {DECREMENT_OF_MIN_INT_PLUS_ONE, false, MIN_INT, null}, // No underflow.
            {DECREMENT_OF_MIN_INT, true, null, "Overflow or underflow"},
            {"print 1000000 * 1000000", true, null, "Overflow or underflow"},
            {"print testFunction(4)", false, "9", null},
            {"print 2 + testFunction(4)", false, "11", null},
            {"print testFunction(testFunction(4))", false, "14", null},
            {"function myFun(x, y) {z = x - y; return z}; print myFun(5, 3)", false, "2", null},
            {"function myFun(x) {return x}; print myFun(2 + 3)", false, "5", null},
            {"function myFun(x) {function innerFun(y) {return 3}; return 5}; print 6", true, null,
                "Nested function definitions not allowed"},
            {"print 5 +", true, null, ""}, // Syntax error.
            {"xyz", true, null, ""}, // Syntax error.
            {"print 5 ** 3", true, null, ""}, // Unknown token.
            {"print x", true, null, ""} // Undefined reference
        });
    }

    @Parameter(0)
    public String input;

    @Parameter(1)
    public boolean expectedHasErrors;

    @Parameter(2)
    public String expectedResult;

    @Parameter(3)
    public String expectedError;

    TestOutputStrategy outputStrategy;

    @Before
    public void setUp() {
        outputStrategy = new TestOutputStrategy();
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
        Assert.assertEquals(expectedHasErrors, outputStrategy.getNumErrors() >= 1);
        if (expectedHasErrors) {
            Assert.assertThat(outputStrategy.getError(0), CoreMatchers.containsString(expectedError));
        } else {
            Assert.assertEquals(expectedResult, outputStrategy.getLine(0));
        }
    }
}
