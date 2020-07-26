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
public class FunctionCallCheckTest extends AstConstructionTestBase {
    @Mock(type = MockType.STRICT)
    public StatusReporter reporter;

    @Test
    public void whenDeclaredFunctionCalledThenNoError() {
        replay(reporter);
        parse("function fun() {return 3}; print fun()");
        Assert.assertFalse(hasParseErrors);
        new FunctionCallCheck(reporter).run(ast);
        verify(reporter);
    }

    @Test
    public void whenUndeclaredFunctionCalledThenError() {
        reporter.report(eq(StatusCode.FUNCTION_DOES_NOT_EXIST), eq(1), anyInt(), eq("fun"));
        replay(reporter);
        parse("print fun(3)");
        Assert.assertFalse(hasParseErrors);
        new FunctionCallCheck(reporter).run(ast);
        verify(reporter);
    }

    @Test
    public void whenSameFunctionDefinedMultipleTimesThenError() {
        reporter.report(eq(StatusCode.FUNCTION_ALREADY_DEFINED), eq(2), anyInt(), eq("fun"));
        replay(reporter);
        parse("function fun() {return 3};\nfunction fun() {return 5}");
        Assert.assertFalse(hasParseErrors);
        new FunctionCallCheck(reporter).run(ast);
        verify(reporter);
    }

    @Test
    public void whenArgumentCountMismatchThenError() {
        reporter.report(
                eq(StatusCode.FUNCTION_ARGUMENT_COUNT_MISMATCH),
                eq(2),
                anyInt(),
                eq("fun"),
                eq("0"),
                eq("1"));
        replay(reporter);
        parse("function fun() {return 3};\nprint fun(3)");
        Assert.assertFalse(hasParseErrors);
        new FunctionCallCheck(reporter).run(ast);
        verify(reporter);        
    }
}
