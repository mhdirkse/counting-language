package com.github.mhdirkse.countlang.steps;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.stream.IntStream;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.MarkUsedStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;

@RunWith(EasyMockRunner.class)
public class DescendantValueHandledTest {
    @Mock(MockType.NICE)
    public ExecutionContext context;

    @Mock(MockType.NICE)
    public AstNodeExecutionFactory factory;

    @Test
    public void whenAssignmentStatementThenDescendantValueHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        AssignmentStatement statement = new AssignmentStatement(1, 1);
        statement.setLhs("x");
        statement.setRhs(childExpression);
        AssignmentStatementCalculation calculation = new AssignmentStatementCalculation(statement);
        replay(context);
        Assert.assertTrue(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }

    @Test
    public void whenCompositeExpressionThenDescendantValueHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        CompositeExpression expression = new CompositeExpression(1, 1);
        expression.addSubExpression(childExpression);
        expression.setOperator(new Operator.OperatorAdd(1, 1));
        expression.setCountlangType(CountlangType.INT);
        CompositeExpressionCalculation calculation = new CompositeExpressionCalculation(expression);
        replay(context);
        Assert.assertTrue(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }

    @Test
    public void whenMarkUsedStatementThenDescendantValueHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        MarkUsedStatement statement = new MarkUsedStatement(1, 1);
        statement.setExpression(childExpression);
        MarkUsedStatementCalculation calculation = new MarkUsedStatementCalculation(statement);
        replay(context);
        Assert.assertTrue(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }

    @Test
    public void whenPrintStatementThenDescendantValueHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        PrintStatement statement = new PrintStatement(1, 1);
        statement.setExpression(childExpression);
        PrintStatementCalculation calculation = new PrintStatementCalculation(statement);
        replay(context);
        Assert.assertTrue(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }

    @Test
    public void whenReturnStatementThenDescendantResultNotHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        ReturnStatement statement = new ReturnStatement(1, 1);
        statement.setExpression(childExpression);
        ReturnStatementCalculation calculation = new ReturnStatementCalculation(statement);
        replay(context);
        Assert.assertFalse(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }

    @Test
    public void whenSimpleDistributionExpressionThenDescendantResultHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        SimpleDistributionExpression expression = new SimpleDistributionExpression(1, 1);
        expression.addScoredValue(childExpression);
        SimpleDistributionExpressionCalculation calculation = new SimpleDistributionExpressionCalculation(expression);
        replay(context);
        Assert.assertTrue(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }

    @Test
    public void whenDistributionWithTotalExpressionThenDescendantResultHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        DistributionExpressionWithTotal expression = new DistributionExpressionWithTotal(1, 1);
        expression.addScoredValue(childExpression);
        SpecialDistributionExpressionCalculation.WithTotal calculation =
                new SpecialDistributionExpressionCalculation.WithTotal(expression);
        replay(context);
        Assert.assertTrue(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }

    @Test
    public void whenDistributionWithUnknownExpressionThenDescendantResultHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        DistributionExpressionWithUnknown expression = new DistributionExpressionWithUnknown(1, 1);
        expression.addScoredValue(childExpression);
        SpecialDistributionExpressionCalculation.WithUnknown calculation =
                new SpecialDistributionExpressionCalculation.WithUnknown(expression);
        replay(context);
        Assert.assertTrue(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }

    @Test
    public void whenFunctionCallExpressionDoingParametersThenDescendantValueHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        FunctionCallExpression expression = new FunctionCallExpression(1, 1);
        expression.addArgument(childExpression);
        FunctionCallExpressionCalculation calculation = new FunctionCallExpressionCalculation(expression);
        replay(context);
        calculation.step(context);
        Assert.assertTrue(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }

    @Test
    public void whenFunctionCallExpressionDoingStatementsThenDescendantValueNotHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        FunctionCallExpression expression = new FunctionCallExpression(1, 1);
        expression.setFunctionName("fun");
        expression.addArgument(childExpression);
        PrintStatement functionStatement = new PrintStatement(1, 1);
        functionStatement.setExpression(childExpression);
        StatementGroup functionStatements = new StatementGroup(1, 1);
        functionStatements.addStatement(functionStatement);
        FunctionDefinitionStatement funDef = new FunctionDefinitionStatement(1, 1);
        funDef.addFormalParameter("x", CountlangType.INT);
        funDef.setStatements(functionStatements);
        FunctionCallExpressionCalculation calculation = new FunctionCallExpressionCalculation(expression);
        ValueExpressionCalculation childCalculation = new ValueExpressionCalculation(childExpression);
        StatementGroupCalculation statementCalculation = new StatementGroupCalculation(functionStatements);
        StepperImpl stepper = new StepperImpl(expression, context, factory);
        expect(factory.create(isA(FunctionCallExpression.class))).andReturn(calculation);
        expect(context.getFunction("fun")).andReturn(funDef);
        expect(factory.create(isA(ValueExpression.class))).andReturn(childCalculation);
        expect(context.onResult(new Integer(5))).andDelegateTo(stepper);
        expect(factory.create(isA(StatementGroup.class))).andReturn(statementCalculation);
        context.pushVariableFrame(StackFrameAccess.HIDE_PARENT);
        context.writeSymbol(eq("x"), eq(new Integer(5)), isA(FormalParameter.class));
        replay(context);
        replay(factory);
        stepper.init();
        IntStream.range(0, 4).forEach(i -> stepper.step());
        Assert.assertFalse(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
        verify(factory);
    }

    @Test
    public void whenIfStatementDoingExpressionsThenDescendantValueHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        IfStatement ifStatement = new IfStatement(1, 1);
        ifStatement.setSelector(childExpression);
        IfStatementCalculation calculation = new IfStatementCalculation(ifStatement);
        replay(context);
        calculation.step(context);
        Assert.assertTrue(calculation.handleDescendantResult(Boolean.TRUE, context));
        verify(context);
    }

    @Test
    public void whenIfStatementDoingStatementsThenDescendantValueNotHandled() {
        ValueExpression childExpression = new ValueExpression(1, 1, new Integer(5));
        IfStatement ifStatement = new IfStatement(1, 1);
        ifStatement.setSelector(childExpression);
        IfStatementCalculation calculation = new IfStatementCalculation(ifStatement);
        replay(context);
        calculation.step(context);
        Assert.assertTrue(calculation.handleDescendantResult(Boolean.TRUE, context));
        // Bring calculation to AFTER
        calculation.step(context);
        Assert.assertFalse(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }

    @Test
    public void whenStatementGroupThenDescendantValueNotHandled() {
        PrintStatement child = new PrintStatement(1, 1);
        StatementGroup statementGroup = new StatementGroup(1, 1);
        statementGroup.addStatement(child);
        StatementGroupCalculation calculation = new StatementGroupCalculation(statementGroup);
        context.pushVariableFrame(StackFrameAccess.SHOW_PARENT);
        replay(context);
        calculation.step(context);
        Assert.assertFalse(calculation.handleDescendantResult(new Integer(5), context));
        verify(context);
    }
}
