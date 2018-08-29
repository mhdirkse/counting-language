package com.github.mhdirkse.countlang.ast;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;

import com.github.mhdirkse.countlang.lang.parsing.ParseEntryPoint;

public class AstConstructionTestBase {
    Program ast = null;
    boolean hasParseErrors = false;

    final void parse(final String program) {
    	try {
    		parseUnchecked(program);
    	}
    	catch (IOException e) {
    		throw new IllegalArgumentException(e);
    	}
    }

    final void parseUnchecked(final String program) throws IOException {
    	Reader reader = new StringReader(program);
    	try {
    	    ParseEntryPoint parser = new ParseEntryPoint();
    		parser.parseProgram(reader);
    		hasParseErrors = parser.hasError();
    	    ast = parser.getParsedNodeAsProgram();
    	}
    	finally {
    		reader.close();
    	}
    }

    final <T extends Statement> T checkStatementType(
            final Statement statement, final Class<T> type) {
        Assert.assertThat(statement, instanceOf(type));
        return type.cast(statement);        
    }

    final <T extends ExpressionNode> T checkExpressionType(
            final ExpressionNode expression, final Class<T> type) {
        Assert.assertThat(expression, instanceOf(type));
        return type.cast(expression);
    }
}
