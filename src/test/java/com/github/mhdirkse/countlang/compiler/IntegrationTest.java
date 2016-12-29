package com.github.mhdirkse.countlang.compiler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.AstProducer;
import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.engine.Engine;
import com.github.mhdirkse.countlang.engine.OutputReceiver;

public class IntegrationTest implements OutputReceiver {
    private List<String> outputs;

    @Before
    public void setUp() {
        outputs = new ArrayList<String>();
    }

    @Override
    public void onOutputLine(final String output) {
        outputs.add(output);
    }

    private void compileAndRun(final String programText) {
        Program ast = new AstProducer().fromString(programText);
        Compiler compiler = new Compiler(this);
        Engine engine = compiler.compile(ast);
        engine.run();
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
