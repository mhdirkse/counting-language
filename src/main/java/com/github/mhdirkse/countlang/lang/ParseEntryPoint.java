package com.github.mhdirkse.countlang.lang;

import java.io.IOException;
import java.io.Reader;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.Program;

public class ParseEntryPoint {
	private AstNode node = null;
	private String error = null;

    public void parseProgram(Reader reader) throws IOException {
        try {
            parseProgramImpl(reader);
        }
        catch(ParseCancellationException e) {
            error = e.getMessage();
        }
    }

    private void parseProgramImpl(Reader reader) throws IOException {
        CountlangLexer lexer = new CountlangLexer(new ANTLRInputStream(reader));
        lexer.removeErrorListeners();
        lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CountlangParser parser = new CountlangParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);
        ParseTreeWalker walker = new ParseTreeWalker();
        AstProducingListener listener = new AstProducingListener();
        walker.walk(listener, parser.prog());
        if (listener.isFinished()) {
            node = listener.getProgram();
        } else {
            throw new IllegalArgumentException("Unexpected end of input");
        }
    }

    public AstNode getParsedNode() {
    	return node;
    }

    public Program getParsedNodeAsProgram() {
        if (! (node instanceof Program)) {
            throw new IllegalStateException("The parsed node was not a Program");
        }
        return (Program) node;
    }

    public String getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }
}
