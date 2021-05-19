/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.execution;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.math.BigInteger;

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
    }

    @Test
    public void testCompositeExpressionCalculation() {
        expect(context.onResult(new BigInteger("5"))).andDelegateTo(stepper);
        expect(context.readSymbol(EasyMock.eq("x"), EasyMock.isA(AstNode.class))).andReturn(new BigInteger("3"));
        expect(context.onResult(new BigInteger("3"))).andDelegateTo(stepper);
        expect(context.onResult(new BigInteger("8"))).andDelegateTo(stepper);
        replay(context);
        stepper.run();
        verify(context);
    }
}
