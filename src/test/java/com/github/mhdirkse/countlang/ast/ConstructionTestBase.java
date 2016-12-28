package com.github.mhdirkse.countlang.ast;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;

import com.github.mhdirkse.countlang.lang.AstProducingListener;
import com.github.mhdirkse.countlang.lang.CountlangLexer;
import com.github.mhdirkse.countlang.lang.CountlangParser;

public class ConstructionTestBase {
    Program ast = null;

    final void parse(String program) {
        CountlangLexer lexer = new CountlangLexer(new ANTLRInputStream(program));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CountlangParser parser = new CountlangParser(tokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        AstProducingListener listener = new AstProducingListener();
        Assert.assertFalse(listener.isFinished());
        walker.walk(listener, parser.prog());
        Assert.assertTrue(listener.isFinished());
        ast = listener.getProgram();
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
