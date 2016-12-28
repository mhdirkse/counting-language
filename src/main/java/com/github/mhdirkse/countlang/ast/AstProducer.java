package com.github.mhdirkse.countlang.ast;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.github.mhdirkse.countlang.lang.AstProducingListener;
import com.github.mhdirkse.countlang.lang.CountlangLexer;
import com.github.mhdirkse.countlang.lang.CountlangParser;

public final class AstProducer {
    public Program fromString(String programText) {
        CountlangLexer lexer = new CountlangLexer(new ANTLRInputStream(programText));
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
