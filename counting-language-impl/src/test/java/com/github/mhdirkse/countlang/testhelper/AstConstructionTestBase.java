package com.github.mhdirkse.countlang.testhelper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.lang.parsing.ParseEntryPoint;

public class AstConstructionTestBase {
    public StatementGroup ast = null;
    public boolean hasParseErrors = false;

    public final void parse(final String program) {
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
    	    ast = parser.getParsedNodeAsStatementGroup();
    	}
    	finally {
    		reader.close();
    	}
    }
}
