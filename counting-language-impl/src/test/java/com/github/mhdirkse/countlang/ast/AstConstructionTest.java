package com.github.mhdirkse.countlang.ast;

import org.junit.Assert;
import org.junit.Test;

public class AstConstructionTest extends AstConstructionTestBase {
    @Test
    public void testSimplePrint() {
        int printedValue = 3;
        String simplePrint = "print " + Integer.toString(printedValue);
        parse(simplePrint);
        Assert.assertFalse(hasParseErrors);
        Assert.assertEquals(1, ast.getSize());
        PrintStatement statement = checkStatementType(
                ast.getStatement(0), PrintStatement.class);
        ValueExpression expression = checkExpressionType(
                statement.getExpression(), ValueExpression.class);
        Assert.assertEquals(printedValue, expression.getValue());
    }

    @Test
    public void testSimpleAssignment() {
        int assignedValue = 5;
        String simpleAssignment = "x = " + Integer.toString(assignedValue);
        parse(simpleAssignment);
        Assert.assertFalse(hasParseErrors);
        Assert.assertEquals(1, ast.getSize());
        AssignmentStatement statement = checkStatementType(
                ast.getStatement(0), AssignmentStatement.class);
        Assert.assertEquals("x", statement.getLhs());
        ValueExpression expression = checkExpressionType(
                statement.getRhs(), ValueExpression.class);
        Assert.assertEquals(assignedValue, expression.getValue());
    }

    @Test
    public void testMultipleStatementsAndSymbolReference() {
        int assignedValue = 7;
        String programText = String.format("z = %d; print z", assignedValue);
        parse(programText);
        Assert.assertFalse(hasParseErrors);
        Assert.assertEquals(2, ast.getSize());
        AssignmentStatement statement1 = checkStatementType(
                ast.getStatement(0), AssignmentStatement.class);
        PrintStatement statement2 = checkStatementType(
                ast.getStatement(1), PrintStatement.class);
        Assert.assertEquals("z", statement1.getLhs());
        ValueExpression expression11 = checkExpressionType(
                statement1.getRhs(), ValueExpression.class);
        SymbolExpression expression22 = checkExpressionType(
                statement2.getExpression(), SymbolExpression.class);
        Assert.assertEquals(assignedValue, expression11.getValue());
        Assert.assertEquals("z", expression22.getSymbol());
    }
}
