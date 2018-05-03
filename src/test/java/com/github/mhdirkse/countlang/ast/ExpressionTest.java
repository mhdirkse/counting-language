package com.github.mhdirkse.countlang.ast;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        Assert.assertEquals(expected, expression.calculate(new ExecutionContext(
                new Scope(), new TestOutputStrategy())).getValue(), expected);
    }

    @Test
    public void testMultTakesPrecedenceOverPlus1() {
        init(String.format("%d + %d * %d", first, second, third));
        checkValue(first + second * third);
        Assert.assertEquals("+", expression.getOperator().getName());
        Assert.assertEquals(2, expression.getNumSubExpressions());
        ValueExpression arg1 = checkExpressionType(
                expression.getSubExpression(0), ValueExpression.class);
        CompositeExpression arg2 = checkExpressionType(
                expression.getSubExpression(1), CompositeExpression.class);
        Assert.assertEquals(first, arg1.getValue().getValue());
        Assert.assertEquals("*", arg2.getOperator().getName());
        Assert.assertEquals(2, arg2.getNumSubExpressions());
        ValueExpression arg21 = checkExpressionType(
                arg2.getSubExpression(0), ValueExpression.class);
        ValueExpression arg22 = checkExpressionType(
                arg2.getSubExpression(1), ValueExpression.class);
        Assert.assertEquals(second, arg21.getValue().getValue());
        Assert.assertEquals(third, arg22.getValue().getValue());
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
        Assert.assertEquals(first, arg11.getValue().getValue());
        Assert.assertEquals(second, arg12.getValue().getValue());
        Assert.assertEquals(third, arg2.getValue().getValue());
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
        Assert.assertEquals(first, arg11.getValue().getValue());
        Assert.assertEquals(second, arg12.getValue().getValue());
        Assert.assertEquals(third, arg2.getValue().getValue());
    }
}
