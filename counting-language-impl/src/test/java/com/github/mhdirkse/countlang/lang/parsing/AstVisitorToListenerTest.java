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

package com.github.mhdirkse.countlang.lang.parsing;

import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstListener;
import com.github.mhdirkse.countlang.ast.AstVisitorToListener;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.DistributionItemItem;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpressionNonMember;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.SampleStatement;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.testhelper.AstConstructionTestBase;

@RunWith(EasyMockRunner.class)
public class AstVisitorToListenerTest extends AstConstructionTestBase {
    @Mock(type = MockType.STRICT)
    AstListener listener;

    private void runProgram(final String programText) {
        parse(programText);
        Assert.assertFalse(hasParseErrors);
        AstVisitorToListener instance = new AstVisitorToListener(listener);
        ast.accept(instance);
    }

    @Test
    public void testPrintOperatorExpression() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterPrintStatement(isA(PrintStatement.class));
        listener.enterCompositeExpression(isA(CompositeExpression.class));
        listener.visitOperator(isA(Operator.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitCompositeExpression(isA(CompositeExpression.class));
        listener.exitPrintStatement(isA(PrintStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("print 5 + 3");
        verify(listener);
    }

    @Test
    public void testFunctionDefAndFunctionCallAndAssignment() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterFunctionDefinitionStatement(isA(FunctionDefinitionStatement.class));
        listener.enterFormalParameters(isA(FormalParameters.class));
        listener.visitFormalParameter(isA(FormalParameter.class));
        listener.exitFormalParameters(isA(FormalParameters.class));
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterReturnStatement(isA(ReturnStatement.class));
        listener.enterCompositeExpression(isA(CompositeExpression.class));
        listener.visitOperator(isA(Operator.class));
        listener.visitSymbolExpression(isA(SymbolExpression.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitCompositeExpression(isA(CompositeExpression.class));
        listener.exitReturnStatement(isA(ReturnStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        listener.exitFunctionDefinitionStatement(isA(FunctionDefinitionStatement.class));
        listener.enterAssignmentStatement(isA(AssignmentStatement.class));
        listener.enterFunctionCallExpressionNonMember(isA(FunctionCallExpressionNonMember.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitFunctionCallExpressionNonMember(isA(FunctionCallExpressionNonMember.class));
        listener.exitAssignmentStatement(isA(AssignmentStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("function fun(int x) {return x + 2}; y = fun(3)");
        verify(listener);
    }

    @Test
    public void testIfWithoutElse() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterIfStatement(isA(IfStatement.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        
        // Then
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterPrintStatement(isA(PrintStatement.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitPrintStatement(isA(PrintStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        
        listener.exitIfStatement(isA(IfStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("if(true) {print 3;}");
        verify(listener);
    }

    @Test
    public void testIfWithElse() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterIfStatement(isA(IfStatement.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        
        // Then
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterPrintStatement(isA(PrintStatement.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitPrintStatement(isA(PrintStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        
        // Else
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterPrintStatement(isA(PrintStatement.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitPrintStatement(isA(PrintStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));

        listener.exitIfStatement(isA(IfStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("if(false) {print 3} else {print 5}");
        verify(listener);
    }

    @Test
    public void testSimpleDistribution() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterPrintStatement(isA(PrintStatement.class));
        listener.enterSimpleDistributionExpression(isA(SimpleDistributionExpression.class));
        listener.enterDistributionItemItem(isA(DistributionItemItem.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitDistributionItemItem(isA(DistributionItemItem.class));
        listener.exitSimpleDistributionExpression(isA(SimpleDistributionExpression.class));
        listener.exitPrintStatement(isA(PrintStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("print distribution 1");
        verify(listener);
    }

    @Test
    public void testDistributionWithTotal() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterPrintStatement(isA(PrintStatement.class));
        listener.enterDistributionExpressionWithTotal(isA(DistributionExpressionWithTotal.class));
        listener.enterDistributionItemItem(isA(DistributionItemItem.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitDistributionItemItem(isA(DistributionItemItem.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitDistributionExpressionWithTotal(isA(DistributionExpressionWithTotal.class));
        listener.exitPrintStatement(isA(PrintStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("print distribution 1 total 1");
        verify(listener);
    }

    @Test
    public void testDistributionWithUnknown() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterPrintStatement(isA(PrintStatement.class));
        listener.enterDistributionExpressionWithUnknown(isA(DistributionExpressionWithUnknown.class));
        listener.enterDistributionItemItem(isA(DistributionItemItem.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitDistributionItemItem(isA(DistributionItemItem.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitDistributionExpressionWithUnknown(isA(DistributionExpressionWithUnknown.class));
        listener.exitPrintStatement(isA(PrintStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("print distribution 1 unknown 1");
        verify(listener);
    }

    @Test
    public void testEmptyDistribution() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterPrintStatement(isA(PrintStatement.class));
        listener.enterSimpleDistributionExpression(isA(SimpleDistributionExpression.class));
        listener.exitSimpleDistributionExpression(isA(SimpleDistributionExpression.class));
        listener.exitPrintStatement(isA(PrintStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("print distribution");
        verify(listener);
    }

    @Test
    public void testExperimentSample() {
        listener.enterStatementGroup(isA(StatementGroup.class));
        listener.enterExperimentDefinitionStatement(isA(ExperimentDefinitionStatement.class));
        listener.enterFormalParameters(isA(FormalParameters.class));
        listener.exitFormalParameters(isA(FormalParameters.class));
        listener.enterStatementGroup(isA(StatementGroup.class));

        listener.enterSampleStatement(isA(SampleStatement.class));
        listener.enterSimpleDistributionExpression(isA(SimpleDistributionExpression.class));
        listener.enterDistributionItemItem(isA(DistributionItemItem.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitDistributionItemItem(isA(DistributionItemItem.class));
        listener.enterDistributionItemItem(isA(DistributionItemItem.class));
        listener.visitValueExpression(isA(ValueExpression.class));
        listener.exitDistributionItemItem(isA(DistributionItemItem.class));
        listener.exitSimpleDistributionExpression(isA(SimpleDistributionExpression.class));
        listener.exitSampleStatement(isA(SampleStatement.class));

        listener.enterReturnStatement(isA(ReturnStatement.class));
        listener.visitSymbolExpression(isA(SymbolExpression.class));
        listener.exitReturnStatement(isA(ReturnStatement.class));

        listener.exitStatementGroup(isA(StatementGroup.class));
        listener.exitExperimentDefinitionStatement(isA(ExperimentDefinitionStatement.class));
        listener.exitStatementGroup(isA(StatementGroup.class));
        replay(listener);
        runProgram("experiment coin() {sample c from distribution 0, 1; return c}");
        verify(listener);
    }
}
