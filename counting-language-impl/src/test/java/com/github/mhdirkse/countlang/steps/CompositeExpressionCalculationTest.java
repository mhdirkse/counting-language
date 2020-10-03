package com.github.mhdirkse.countlang.steps;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class CompositeExpressionCalculationTest {
    private StepperImpl<Object> stepper;

    @Mock(type = MockType.STRICT)
    private ExecutionContext<Object> context;

    @Before
    public void setUp() {
        stepper = new StepperImpl<Object>(Target.getCompositeExpression(), context, new AstNodeExecutionFactoryCalculate());
    }

    @Test
    public void testCompositeExpressionCalculation() {
        expect(context.onResult(5)).andDelegateTo(stepper);
        expect(context.readSymbol("x")).andReturn(3);
        expect(context.onResult(3)).andDelegateTo(stepper);
        expect(context.onResult(8)).andDelegateTo(stepper);
        replay(context);
        stepper.run();
        verify(context);
    }
}
