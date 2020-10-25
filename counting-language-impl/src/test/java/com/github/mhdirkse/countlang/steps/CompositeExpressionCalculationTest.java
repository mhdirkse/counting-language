package com.github.mhdirkse.countlang.steps;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.AstNode;

@RunWith(EasyMockRunner.class)
public class CompositeExpressionCalculationTest {
    private StepperImpl stepper;

    @Mock(type = MockType.STRICT)
    private ExecutionContext context;

    @Before
    public void setUp() {
        stepper = new StepperImpl(Target.getCompositeExpression(), context, new AstNodeExecutionFactoryCalculate());
        stepper.init();
    }

    @Test
    public void testCompositeExpressionCalculation() {
        expect(context.onResult(5)).andDelegateTo(stepper);
        expect(context.readSymbol(EasyMock.eq("x"), EasyMock.isA(AstNode.class))).andReturn(3);
        expect(context.onResult(3)).andDelegateTo(stepper);
        expect(context.onResult(8)).andDelegateTo(stepper);
        replay(context);
        stepper.run();
        verify(context);
    }
}
