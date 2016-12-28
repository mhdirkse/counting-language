package com.github.mhdirkse.countlang.ast;

import org.junit.Assert;
import org.junit.Test;

public class ConstructionTest extends ConstructionTestBase {
    @Test
    public void testSimplePrint() {
        int printedValue = 3;
        String simplePrint = "print " + Integer.toString(printedValue);
        parse(simplePrint);
        Assert.assertEquals(1, ast.getSize());
        PrintStatement statement = checkStatementType(
                ast.getStatement(0), PrintStatement.class);
        ValueExpression expression = checkExpressionType(
                statement.getExpression(), ValueExpression.class);
        Assert.assertEquals(printedValue, expression.getValue().getValue());
    }

    @Test
    public void testSimpleAssignment() {
        int assignedValue = 5;
        String simpleAssignment = "x = " + Integer.toString(assignedValue);
        parse(simpleAssignment);
        Assert.assertEquals(1, ast.getSize());
        AssignmentStatement statement = checkStatementType(
                ast.getStatement(0), AssignmentStatement.class);
        Assert.assertEquals("x", statement.getLhs().getName());
        ValueExpression expression = checkExpressionType(
                statement.getRhs(), ValueExpression.class);
        Assert.assertEquals(assignedValue, expression.getValue().getValue());
    }

    @Test
    public void testOperatorPrecedence1() {
        int first = 2;
        int second = 4;
        int third = 6;
        String programText = String.format(
                "y = %d + %d * %d", first, second, third);
        parse(programText);
        Assert.assertEquals(1, ast.getSize());
        AssignmentStatement statement = checkStatementType(
                ast.getStatement(0), AssignmentStatement.class);
        Assert.assertEquals("y", statement.getLhs().getName());
        CompositeExpression rhs = checkExpressionType(
                statement.getRhs(), CompositeExpression.class);
        Assert.assertEquals("+", rhs.getOperator().getName());
        Assert.assertEquals(2, rhs.getNumArguments());
        ValueExpression arg1 = checkExpressionType(
                rhs.getArgument(0), ValueExpression.class);
        CompositeExpression arg2 = checkExpressionType(
                rhs.getArgument(1), CompositeExpression.class);
        Assert.assertEquals(first, arg1.getValue().getValue());
        Assert.assertEquals("*", arg2.getOperator().getName());
        Assert.assertEquals(2, arg2.getNumArguments());
        ValueExpression arg21 = checkExpressionType(
                arg2.getArgument(0), ValueExpression.class);
        ValueExpression arg22 = checkExpressionType(
                arg2.getArgument(1), ValueExpression.class);
        Assert.assertEquals(second, arg21.getValue().getValue());
        Assert.assertEquals(third, arg22.getValue().getValue());
    }

    @Test
    public void testOperatorPrecedence2() {
        int first = 2;
        int second = 4;
        int third = 6;
        String programText = String.format(
                "y = %d * %d + %d", first, second, third);
        parse(programText);
        Assert.assertEquals(1, ast.getSize());
        AssignmentStatement statement = checkStatementType(
                ast.getStatement(0), AssignmentStatement.class);
        Assert.assertEquals("y", statement.getLhs().getName());
        CompositeExpression rhs = checkExpressionType(
                statement.getRhs(), CompositeExpression.class);
        Assert.assertEquals("+", rhs.getOperator().getName());
        Assert.assertEquals(2, rhs.getNumArguments());
        CompositeExpression arg1 = checkExpressionType(
                rhs.getArgument(0), CompositeExpression.class);
        ValueExpression arg2 = checkExpressionType(
                rhs.getArgument(1), ValueExpression.class);
        Assert.assertEquals("*", arg1.getOperator().getName());
        Assert.assertEquals(2, arg1.getNumArguments());
        ValueExpression arg11 = checkExpressionType(
                arg1.getArgument(0), ValueExpression.class);
        ValueExpression arg12 = checkExpressionType(
                arg1.getArgument(1), ValueExpression.class);
        Assert.assertEquals(first, arg11.getValue().getValue());
        Assert.assertEquals(second, arg12.getValue().getValue());
        Assert.assertEquals(third, arg2.getValue().getValue());
    }

    @Test
    public void testSequenceForEqualPriority() {
        int first = 2;
        int second = 4;
        int third = 6;
        String programText = String.format(
                "y = %d - %d + %d", first, second, third);
        parse(programText);
        Assert.assertEquals(1, ast.getSize());
        AssignmentStatement statement = checkStatementType(
                ast.getStatement(0), AssignmentStatement.class);
        Assert.assertEquals("y", statement.getLhs().getName());
        CompositeExpression rhs = checkExpressionType(
                statement.getRhs(), CompositeExpression.class);
        Assert.assertEquals("+", rhs.getOperator().getName());
        Assert.assertEquals(2, rhs.getNumArguments());
        CompositeExpression arg1 = checkExpressionType(
                rhs.getArgument(0), CompositeExpression.class);
        ValueExpression arg2 = checkExpressionType(
                rhs.getArgument(1), ValueExpression.class);
        Assert.assertEquals("-", arg1.getOperator().getName());
        Assert.assertEquals(2, arg1.getNumArguments());
        ValueExpression arg11 = checkExpressionType(
                arg1.getArgument(0), ValueExpression.class);
        ValueExpression arg12 = checkExpressionType(
                arg1.getArgument(1), ValueExpression.class);
        Assert.assertEquals(first, arg11.getValue().getValue());
        Assert.assertEquals(second, arg12.getValue().getValue());
        Assert.assertEquals(third, arg2.getValue().getValue());
    }

    @Test
    public void testMultipleStatementsAndSymbolReference() {
        int assignedValue = 7;
        String programText = String.format("z = %d; print z", assignedValue);
        parse(programText);
        Assert.assertEquals(2, ast.getSize());
        AssignmentStatement statement1 = checkStatementType(
                ast.getStatement(0), AssignmentStatement.class);
        PrintStatement statement2 = checkStatementType(
                ast.getStatement(1), PrintStatement.class);
        Assert.assertEquals("z", statement1.getLhs().getName());
        ValueExpression expression11 = checkExpressionType(
                statement1.getRhs(), ValueExpression.class);
        SymbolExpression expression22 = checkExpressionType(
                statement2.getExpression(), SymbolExpression.class);
        Assert.assertEquals(assignedValue, expression11.getValue().getValue());
        Assert.assertEquals("z", expression22.getSymbol().getName());
    }
}
