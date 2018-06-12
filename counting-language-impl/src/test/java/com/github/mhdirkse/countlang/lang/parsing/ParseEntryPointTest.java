package com.github.mhdirkse.countlang.lang.parsing;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.SymbolExpression;

public class ParseEntryPointTest {
    AstNode fakeNode;
    ParseEntryPoint instance;

    @Before
    public void setUp() {
        fakeNode = new SymbolExpression(1, 1);
        instance = new ParseEntryPoint();
        instance.node = fakeNode;
    }

    @Test(expected = IllegalStateException.class)
    public void testWhenNotParsedProgramThenGetParsedNodeAsProgramFalse() {
        instance.getParsedNodeAsProgram();
    }
}
