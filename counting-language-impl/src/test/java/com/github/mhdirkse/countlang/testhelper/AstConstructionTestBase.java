/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
