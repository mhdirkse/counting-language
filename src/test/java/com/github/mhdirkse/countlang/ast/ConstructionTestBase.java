package com.github.mhdirkse.countlang.ast;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.junit.Assert;

public class ConstructionTestBase {
    Program ast = null;

    final void parse(String program) {
        ast = new AstProducer().fromString(program);
    }

    final <T extends Statement> T checkStatementType(
            final Statement statement, final Class<T> type) {
        Assert.assertThat(statement, instanceOf(type));
        return type.cast(statement);        
    }

    final <T extends Expression> T checkExpressionType(
            final Expression expression, final Class<T> type) {
        Assert.assertThat(expression, instanceOf(type));
        return type.cast(expression);
    }
}
