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

package com.github.mhdirkse.countlang.lang.parsing;

import java.io.IOException;
import java.io.Reader;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.lang.CountlangLexer;
import com.github.mhdirkse.countlang.lang.CountlangParser;

public class ParseEntryPoint {
	AstNode node = null;
	private String error = null;

    public void parseProgram(Reader reader) throws IOException {
        try {
            parseProgramImpl(reader);
        }
        catch(ParseCancellationException e) {
            error = e.getMessage();
        }
        catch(ProgramException e) {
            error = e.getMessage();
        }
    }

    private void parseProgramImpl(final Reader reader) throws IOException {
        CountlangLexer lexer = new CountlangLexer(new ANTLRInputStream(reader));
        lexer.removeErrorListeners();
        lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CountlangParser parser = new CountlangParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);
        ParseTreeWalker walker = new ParseTreeWalker();
        IgnoredMethodsHandler ignoredMethodsHandler = new IgnoredMethodsHandler();
        RootHandler rootHandler = new RootHandler();
        CountlangListenerDelegator delegator = new CountlangListenerDelegator(ignoredMethodsHandler, rootHandler);
        walker.walk(delegator, parser.prog());
        node = rootHandler.getProgram();
    }

    public StatementGroup getParsedNodeAsStatementGroup() {
        if (! (node instanceof StatementGroup)) {
            throw new IllegalStateException("The parsed node was not a StatementGroup");
        }
        return (StatementGroup) node;
    }

    public String getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }
}
