package com.github.mhdirkse.countlang.tasks;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.testhelper.AstConstructionTestBase;

import org.junit.Assert;

import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class FunctionAndReturnCheckTest extends AstConstructionTestBase {
    @Mock(type = MockType.STRICT)
    public StatusReporter reporter;

    @Test
    public void whenOnlyNormalStatementsThenNoError() {
        replay(reporter);
        parse("x = 3 + 5; print x");
        Assert.assertFalse(hasParseErrors);
        new FunctionAndReturnCheck(ast, reporter).run();
        verify(reporter);
    }

    @Test
    public void whenReturnAtTopLevelThenError() {
        reporter.report(eq(StatusCode.RETURN_OUTSIDE_FUNCTION), eq(1), anyInt());
        replay(reporter);
        parse("x = 3; return x");
        Assert.assertFalse(hasParseErrors);
        new FunctionAndReturnCheck(ast, reporter).run();
        verify(reporter);
    }

    @Test
    public void whenReturnInFunctionThenNoError() {
        replay(reporter);
        parse("function fun(int x) {return x}");
        Assert.assertFalse(hasParseErrors);
        new FunctionAndReturnCheck(ast, reporter).run();
        verify(reporter);
    }

    @Test
    public void whenReturnInBlockStatementInFunctionThenNoError() {
        replay(reporter);
        parse("function fun() { {x = 3; return x} }");
        Assert.assertFalse(hasParseErrors);
        new FunctionAndReturnCheck(ast, reporter).run();
        verify(reporter);
    }

    @Test
    public void whenNoReturnInFunctionThenError() {
        reporter.report(eq(StatusCode.FUNCTION_DOES_NOT_RETURN), eq(1), anyInt(), eq("fun"));
        replay(reporter);
        parse("function fun(int x) {y = x}");
        Assert.assertFalse(hasParseErrors);
        new FunctionAndReturnCheck(ast, reporter).run();
        verify(reporter);
    }

    @Test
    public void whenStatementsAfterReturnThenError() {
        reporter.report(eq(StatusCode.FUNCTION_STATEMENT_WITHOUT_EFFECT), eq(2), anyInt(), eq("fun"));
        reporter.report(eq(StatusCode.FUNCTION_HAS_EXTRA_RETURN), eq(3), anyInt(), eq("fun"));
        replay(reporter);
        parse("function fun(int x) {return 3;\nprint 5;\nreturn 5}");
        Assert.assertFalse(hasParseErrors);
        new FunctionAndReturnCheck(ast, reporter).run();
        verify(reporter);
    }

    @Test
    public void whenNestedFunctionsThenError() {
        reporter.report(eq(StatusCode.FUNCTION_NESTED_NOT_ALLOWED), eq(1), anyInt());
        replay(reporter);
        parse("function fun(int x) {function nested(int y) {return y}; return x}");
        Assert.assertFalse(hasParseErrors);
        new FunctionAndReturnCheck(ast, reporter).run();
        verify(reporter);
    }

    @Test
    public void whenInFunctionReturnAfterBlockStatementReturnThenError() {
        reporter.report(eq(StatusCode.FUNCTION_HAS_EXTRA_RETURN), eq(1), anyInt(), eq("fun"));
        replay(reporter);
        parse("function fun() { {return 3}; return 5}");
        Assert.assertFalse(hasParseErrors);
        new FunctionAndReturnCheck(ast, reporter).run();
        verify(reporter);
    }
}
