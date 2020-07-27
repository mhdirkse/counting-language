package com.github.mhdirkse.countlang.tasks;

import static com.github.mhdirkse.countlang.tasks.Constants.DECREMENT_OF_MIN_INT;
import static com.github.mhdirkse.countlang.tasks.Constants.INCREMENT_OF_MAX_INT;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.TestOutputStrategy;

@RunWith(Parameterized.class)
public class IntegrationUnhappyTest implements OutputStrategy
{
    @Parameters(name = "{0}, expect errors {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {

            // Calculating
            
            {"print 2 / 0", "Division by zero"},
            {"print 12345678901234567890", "Integer value is too big to store"},
            {INCREMENT_OF_MAX_INT, "Overflow or underflow"},
            {DECREMENT_OF_MIN_INT, "Overflow or underflow"},
            {"print 1000000 * 1000000", "Overflow or underflow"},
            
            // Syntax
            
            {"print 5 +", ""}, // Syntax error.
            {"xyz", ""}, // Syntax error.
            {"print 5 ** 3", ""}, // Unknown token.
            
            // Variable references and type checking
            
            {"print x", ""}, // Undefined reference
            {"print true and 5", "Type mismatch using operator"},
            {"print 5 + true", "Type mismatch using operator"},
            {"print 3 == true", "Type mismatch using operator"},
            {"function fun(int x) {return x}; print fun(true)", "Type mismatch calling function"},
            {"x = true; print x; x = 5; print x;", "Cannot change type of variable"},
            
            // Functions
            
            {"function fun(int x, int y) {return x + y}; print fun(5)", "Argument count mismatch"},
            {"print fun(5);", "does not exist"},
            {"function fun() {return 3}; function fun() {return 5}", "was already defined"},
            {"return 3", "Return statement outside function"},
            {"function fun() {return 3; return 5}; print fun()", "has extra return statement"},
            {"function fun() {return 3; print 5}; print fun()", "has no effect"},
            {"function fun() {print 3}; print fun()", "does not return a value"},
            {"function fun() {function fun2() {return 3}; return 5}", "Nested functions not allowed"},
            
            // Compound
            
            {"{x = 3; markUsed x}; print x", "Undefined"}, // When variable does not exist, it becomes local and is lost
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
	    	new ExecuteProgramTask(reader).run(this);
    	}
    	finally {
    		reader.close();
    	}
    }

    @Test
    public void test() {
        compileAndRun(input);
        Assert.assertEquals(1, outputStrategy.getNumErrors());
        Assert.assertThat(outputStrategy.getError(0), CoreMatchers.containsString(expectedError));
    }
}
