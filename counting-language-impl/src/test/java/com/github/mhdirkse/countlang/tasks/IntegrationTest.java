package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

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
    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"print 5 + 3", false, "8"},
            {"print 5 - 3", false, "2"},
            {"print 5 * 3", false, "15"},
            {"print 15 / 3", false, "5"},
            {"print testFunction(4)", false, "9"},
            {"print 2 + testFunction(4)", false, "11"},
            {"print testFunction(testFunction(4))", false, "14"},
            {"function myFun(x, y) {z = x - y; return z}; print myFun(5, 3)", false, "2"},
            {"function myFun(x) {return x}; print myFun(2 + 3)", false, "5"},
            {"print 5 +", true, null}, // Syntax error
            {"xyz", true, null}, // Syntax error
            {"print 5 ** 3", true, null}, // Unknown token
            {"print x", true, null} // Undefined reference
        });
    }

    @Parameter(0)
    public String input;

    @Parameter(1)
    public boolean expectedHasErrors;

    @Parameter(2)
    public String expectedResult;

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
        if (!expectedHasErrors) {
            Assert.assertEquals(expectedResult, outputStrategy.getLine(0));
        }
    }
}
