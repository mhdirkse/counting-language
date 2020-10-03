package com.github.mhdirkse.countlang.steps;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.tasks.StatusCode;

@RunWith(EasyMockRunner.class)
public class CompositeExpressionTypeCheckTest {
    private StepperImpl<CountlangType> stepper;
    private CompositeExpression target;

    @Mock(type = MockType.STRICT)
    private ExecutionContext<CountlangType> context;

    @Before
    public void setUp() {
        AstNodeExecutionFactory<CountlangType> factory = new AstNodeExecutionFactoryTypeCheck();
        target = Target.getCompositeExpression();
        stepper = new StepperImpl<CountlangType>(target, context, factory);
    }

    @Test
    public void testCompositeExpressionTypeCheck() {
        expect(context.onResult(CountlangType.INT)).andDelegateTo(stepper);
        expect(context.readSymbol("x")).andReturn(CountlangType.INT);
        expect(context.onResult(CountlangType.INT)).andDelegateTo(stepper);
        expect(context.onResult(CountlangType.INT)).andDelegateTo(stepper);
        replay(context);
        stepper.run();
        verify(context);
        Assert.assertFalse(stepper.hasMoreSteps());
        Assert.assertEquals(CountlangType.INT, target.getCountlangType());
        Assert.assertEquals(CountlangType.INT, target.getSubExpression(0).getCountlangType());
        Assert.assertEquals(CountlangType.INT, target.getSubExpression(1).getCountlangType());
    }

    @Test
    public void testWhenOperatorIncompatibleTypesThenErrorReported() {
        expect(context.onResult(CountlangType.INT)).andDelegateTo(stepper);
        expect(context.readSymbol("x")).andReturn(CountlangType.BOOL);
        expect(context.onResult(CountlangType.BOOL)).andDelegateTo(stepper);
        context.report(
                EasyMock.eq(StatusCode.OPERATOR_TYPE_MISMATCH),
                EasyMock.anyInt(),
                EasyMock.anyInt(),
                EasyMock.isA(String.class));
        expect(context.onResult(CountlangType.UNKNOWN)).andDelegateTo(stepper);
        replay(context);
        stepper.run();
        verify(context);
    }
}
