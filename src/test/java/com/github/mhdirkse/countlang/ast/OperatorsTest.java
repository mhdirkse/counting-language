package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import org.junit.Assert;

@RunWith(Parameterized.class)
public class OperatorsTest extends AstConstructionTestBase {
    private static int first = 5;
    private static int second = 3;

    @Parameters(name = "Operator {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {getProgramForOperator("+"), "+"},
            {getProgramForOperator("-"), "-"},
            {getProgramForOperator("*"), "*"},
            {getProgramForOperator("/"), "/"}
        });
    }

    private static String getProgramForOperator(final String operator) {
        return String.format("print %d %s %d", first, operator, second); 
    }

    @Parameter(value = 0)
    public String programText;

    @Parameter(value = 1)
    public String expectedOperator;

    @Test
    public void test() {
        parse(programText);
        Assert.assertFalse(hasParseErrors);
        Assert.assertEquals(1, ast.getSize());
        PrintStatement statement = checkStatementType(
                ast.getStatement(0), PrintStatement.class);
        CompositeExpression expression = checkExpressionType(
                statement.getExpression(), CompositeExpression.class);
        Assert.assertEquals(expectedOperator, expression.getOperator().getName());
        Assert.assertEquals(2, expression.getNumSubExpressions());
        ValueExpression expression1 = checkExpressionType(
                expression.getSubExpression(0), ValueExpression.class);
        ValueExpression expression2 = checkExpressionType(
                expression.getSubExpression(1), ValueExpression.class);
        Assert.assertEquals(first, expression1.getValue().getValue());
        Assert.assertEquals(second, expression2.getValue().getValue());
    }
}
