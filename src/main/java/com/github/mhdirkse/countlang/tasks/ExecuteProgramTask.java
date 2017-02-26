package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.Reader;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.github.mhdirkse.countlang.ast.ExecutionContext;
import com.github.mhdirkse.countlang.ast.OutputStrategy;
import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.Scope;
import com.github.mhdirkse.countlang.lang.AstProducingListener;
import com.github.mhdirkse.countlang.lang.CountlangLexer;
import com.github.mhdirkse.countlang.lang.CountlangParser;

public class ExecuteProgramTask implements AbstractTask {
    private final Reader reader;

    public ExecuteProgramTask(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void run(final OutputContext outputContext) throws IOException {
        getProgram().execute(getExecutionContext(outputContext));
    }

    private Program getProgram() throws IOException {
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

    private ExecutionContext getExecutionContext(final OutputContext outputContext) {
        return new ExecutionContext(new Scope(), getOutputStrategy(outputContext));
    }

    private OutputStrategy getOutputStrategy(final OutputContext outputContext) {
        return new OutputStrategy() {
            @Override
            public void output(String s) {
                outputContext.output(s);
            }
        };
    }
}
