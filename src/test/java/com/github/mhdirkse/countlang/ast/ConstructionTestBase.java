package com.github.mhdirkse.countlang.ast;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;

import com.github.mhdirkse.countlang.lang.ParseEntryPoint;

public class ConstructionTestBase {
    Program ast = null;

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
    		ast = ParseEntryPoint.getProgram(reader);
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

    final <T extends Expression> T checkExpressionType(
            final Expression expression, final Class<T> type) {
        Assert.assertThat(expression, instanceOf(type));
        return type.cast(expression);
    }
}
