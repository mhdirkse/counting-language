package com.github.mhdirkse.countlang.lang;

import java.io.IOException;
import java.io.Reader;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.github.mhdirkse.countlang.ast.Program;

public class ParseEntryPoint {
	private ParseEntryPoint() {
	}

    public static Program getProgram(Reader reader) throws IOException {
        CountlangLexer lexer = new CountlangLexer(new ANTLRInputStream(reader));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CountlangParser parser = new CountlangParser(tokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        AstProducingListener listener = new AstProducingListener();
        walker.walk(listener, parser.prog());
        if (listener.isFinished()) {
            return listener.getProgram();
        } else {
            throw new IllegalArgumentException("Unexpected end of input");
        }
    }
}
