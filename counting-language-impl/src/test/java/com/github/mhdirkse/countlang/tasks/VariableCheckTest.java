package com.github.mhdirkse.countlang.tasks;

import static org.easymock.EasyMock.*;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.AstConstructionTestBase;

@RunWith(EasyMockRunner.class)
public class VariableCheckTest extends AstConstructionTestBase {
    @Mock
    StatusReporter reporter;

    @Test
    public void whenNoErrorThenNothingReported() {
        replay(reporter);
        VariableCheck instance = new VariableCheck(reporter);
        parse("x = 5; print x");
        Assert.assertFalse(hasParseErrors);
        instance.run(ast);
        verify(reporter);
    }

    @Test
    public void whenUndefinedVariableUsedThenReported() {
        reporter.report(eq(StatusCode.VAR_UNDEFINED), eq(1), anyInt(), eq("xyz"));
        replay(reporter);
        VariableCheck instance = new VariableCheck(reporter);
        parse("print xyz");
        Assert.assertFalse(hasParseErrors);
        instance.run(ast);
        verify(reporter);
    }

    @Test
    public void whenVariableNotUsedThenReported() {
        reporter.report(eq(StatusCode.VAR_NOT_USED), eq(1), anyInt(), eq("xyz"));
        replay(reporter);
        VariableCheck instance = new VariableCheck(reporter);
        parse("xyz = 5; print 5");
        Assert.assertFalse(hasParseErrors);
        instance.run(ast);
        verify(reporter);
    }

    @Test
    public void whenVariableIsFormalParameterThenUsed() {
        replay(reporter);
        VariableCheck instance = new VariableCheck(reporter);
        parse("xyz = 5; function fun(int x) {return x}; print fun(xyz)");
        Assert.assertFalse(hasParseErrors);
        instance.run(ast);
        verify(reporter);        
    }

    @Test
    public void testLocalAndGlobalVariablesAreDistinguished() {
        reporter.report(eq(StatusCode.VAR_UNDEFINED), eq(1), anyInt(), eq("uvw"));
        reporter.report(eq(StatusCode.VAR_NOT_USED), eq(1), anyInt(), eq("xyz"));
        replay(reporter);
        VariableCheck instance = new VariableCheck(reporter);
        parse("xyz = 3; function fun(int abc) {print abc; xyz = 3; uvw = 5; print xyz; print uvw}; print uvw");
        Assert.assertFalse(hasParseErrors);
        instance.run(ast);
        verify(reporter);        
    }

    @Test
    public void testWhenVariableIsUpdatedUsingSelfThenNoError() {
        replay(reporter);
        VariableCheck instance = new VariableCheck(reporter);
        parse("xyz = 3; xyz = xyz + 1; print xyz");
        Assert.assertFalse(hasParseErrors);
        instance.run(ast);
        verify(reporter);        
    }
}
