package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.OutputStrategy;

public class IntegrationTest implements OutputStrategy
{
    private List<String> outputs;

    @Before
    public void setUp() {
        outputs = new ArrayList<String>();
    }

    @Override
    public void output(final String output) {
        outputs.add(output);
    }

    @Override
    public void error(final String error) {
    	throw new IllegalArgumentException("Did not expect an error: " + error);
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

    private void checkOneLine(String expected) {
        Assert.assertEquals(1, outputs.size());
        Assert.assertEquals(expected, outputs.get(0));
    }

    @Test
    public void testPrintPlus() {
        compileAndRun("print 5 + 3");
        checkOneLine("8");
    }

    @Test
    public void testPrintMinus() {
        compileAndRun("print 5 - 3");
        checkOneLine("2");
    }

    @Test
    public void testPrintMultiply() {
        compileAndRun("print 5 * 3");
        checkOneLine("15");
    }

    @Test
    public void testPrintDivide() {
        compileAndRun("print 15 / 3");
        checkOneLine("5");
    }

    @Test
    public void testAssign() {
        compileAndRun("x = 5 * 3; print x");
        checkOneLine("15");
    }
}
