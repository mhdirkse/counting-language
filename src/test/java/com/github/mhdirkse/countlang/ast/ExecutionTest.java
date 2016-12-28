package com.github.mhdirkse.countlang.ast;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExecutionTest {
    private TestOutputStrategy outputStrategy = null;
    private Scope scope = null;
    
    @Before
    public void setUp() {
        outputStrategy = new TestOutputStrategy();
        scope = new Scope();
    }

    private void execute(String programText) {
        Program program = new AstProducer().fromString(programText);
        program.execute(new ExecutionContext(scope, outputStrategy));
    }

    private void checkOneLine(String expectedLine) {
        Assert.assertEquals(1, outputStrategy.getNumLines());
        Assert.assertEquals(expectedLine, outputStrategy.getLine(0));
    }

    @Test
    public void testPlus() {
        execute("print 3 + 5");
        checkOneLine("8");
    }

    @Test
    public void testMinus() {
        execute("print 3 - 5");
        checkOneLine("-2");
    }

    @Test
    public void testMultiply() {
        execute("print 3 * 5");
        checkOneLine("15");
    }

    @Test
    public void testDivide() {
        execute("print 3 / 5");
        checkOneLine("0");
    }

    @Test
    public void testSymbolCalculation() {
        execute("x = 3 + 5; print x");
        checkOneLine("8");
    }
}
