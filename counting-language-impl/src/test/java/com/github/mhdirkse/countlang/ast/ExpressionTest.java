package com.github.mhdirkse.countlang.ast;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.execution.ExecutionContextImpl;
import com.github.mhdirkse.countlang.execution.TestOutputStrategy;

public class ExpressionTest extends AstConstructionTestBase {
    private static int first = 2;
    private static int second = 4;
    private static int third = 6;

    private CompositeExpression expression;

    @Before
    public void setUp() {
        expression = null;
    }

    private void init(final String input) {
        String programText = "y = " + input;
        parse(programText);
        Assert.assertFalse(hasParseErrors);
        Assert.assertEquals(1, ast.getSize());
        AssignmentStatement assignmentStatement = checkStatementType(
                ast.getStatement(0), AssignmentStatement.class);
        expression = checkExpressionType(assignmentStatement.getRhs(), CompositeExpression.class);
    }

    private void checkValue(final int expected) {
        Assert.assertEquals(expected, ((Integer) (
        		expression.calculate(new ExecutionContextImpl(new TestOutputStrategy()))
        )).intValue());
    }

    @Test
    public void testMultTakesPrecedenceOverPlus1() {
        init(String.format("%d + %d * %d", first, second, third));
        commonChecksPlusThenMult();
    }

    @Test
    public void testSuperfluousBracketsDoNotChangeAst() {
        init(String.format("%d + (%d * %d)", first, second, third));
        commonChecksPlusThenMult();
    }

    private void commonChecksPlusThenMult() {
        checkValue(first + second * third);
        Assert.assertEquals("+", expression.getOperator().getName());
        Assert.assertEquals(2, expression.getNumSubExpressions());
        ValueExpression arg1 = checkExpressionType(
                expression.getSubExpression(0), ValueExpression.class);
        CompositeExpression arg2 = checkExpressionType(
                expression.getSubExpression(1), CompositeExpression.class);
        Assert.assertEquals(first, ((Integer) arg1.getValue()).intValue());
        Assert.assertEquals("*", arg2.getOperator().getName());
        Assert.assertEquals(2, arg2.getNumSubExpressions());
        ValueExpression arg21 = checkExpressionType(
                arg2.getSubExpression(0), ValueExpression.class);
        ValueExpression arg22 = checkExpressionType(
                arg2.getSubExpression(1), ValueExpression.class);
        Assert.assertEquals(second, ((Integer) arg21.getValue()).intValue());
        Assert.assertEquals(third, ((Integer) arg22.getValue()).intValue());
    }

    @Test
    public void testMultTakesPrecedenceOverPlus2() {
        init(String.format("%d * %d + %d", first, second, third));
        checkValue(first * second + third);
        Assert.assertEquals("+", expression.getOperator().getName());
        Assert.assertEquals(2, expression.getNumSubExpressions());
        CompositeExpression arg1 = checkExpressionType(
                expression.getSubExpression(0), CompositeExpression.class);
        ValueExpression arg2 = checkExpressionType(
                expression.getSubExpression(1), ValueExpression.class);
        Assert.assertEquals("*", arg1.getOperator().getName());
        Assert.assertEquals(2, arg1.getNumSubExpressions());
        ValueExpression arg11 = checkExpressionType(
                arg1.getSubExpression(0), ValueExpression.class);
        ValueExpression arg12 = checkExpressionType(
                arg1.getSubExpression(1), ValueExpression.class);
        Assert.assertEquals(first, ((Integer) arg11.getValue()).intValue());
        Assert.assertEquals(second, ((Integer) arg12.getValue()).intValue());
        Assert.assertEquals(third, ((Integer) arg2.getValue()).intValue());
    }

    @Test
    public void testBracketedExpressionTakesPrecedenceOverMult2() {
        init(String.format("%d * (%d + %d)", first, second, third));
        checkValue(first * (second + third));
        Assert.assertEquals("*", expression.getOperator().getName());
        Assert.assertEquals(2, expression.getNumSubExpressions());
        ValueExpression arg1 = checkExpressionType(expression.getSubExpression(0), ValueExpression.class);
        CompositeExpression arg2 = checkExpressionType(expression.getSubExpression(1), CompositeExpression.class);
        Assert.assertEquals(2, arg2.getNumSubExpressions());
        ValueExpression arg21 = checkExpressionType(arg2.getSubExpression(0), ValueExpression.class);
        ValueExpression arg22 = checkExpressionType(arg2.getSubExpression(1), ValueExpression.class);
        Assert.assertEquals(first, ((Integer) arg1.getValue()).intValue());
        Assert.assertEquals(second, ((Integer) arg21.getValue()).intValue());
        Assert.assertEquals(third, ((Integer) arg22.getValue()).intValue());
    }

    @Test
    public void testFirstTakesPrecedenceForEqualOperatorPriorities() {
        init(String.format("%d - %d + %d", first, second, third));
        checkValue(first - second + third);
        Assert.assertEquals("+", expression.getOperator().getName());
        Assert.assertEquals(2, expression.getNumSubExpressions());
        CompositeExpression arg1 = checkExpressionType(
                expression.getSubExpression(0), CompositeExpression.class);
        ValueExpression arg2 = checkExpressionType(
                expression.getSubExpression(1), ValueExpression.class);
        Assert.assertEquals("-", arg1.getOperator().getName());
        Assert.assertEquals(2, arg1.getNumSubExpressions());
        ValueExpression arg11 = checkExpressionType(
                arg1.getSubExpression(0), ValueExpression.class);
        ValueExpression arg12 = checkExpressionType(
                arg1.getSubExpression(1), ValueExpression.class);
        Assert.assertEquals(first, ((Integer) arg11.getValue()).intValue());
        Assert.assertEquals(second, ((Integer) arg12.getValue()).intValue());
        Assert.assertEquals(third, ((Integer) arg2.getValue()).intValue());
    }
}
