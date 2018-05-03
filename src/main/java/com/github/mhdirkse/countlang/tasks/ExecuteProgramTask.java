package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.Reader;

import com.github.mhdirkse.countlang.ast.ExecutionContext;
import com.github.mhdirkse.countlang.ast.OutputStrategy;
import com.github.mhdirkse.countlang.ast.Scope;
import com.github.mhdirkse.countlang.lang.ParseEntryPoint;

public class ExecuteProgramTask implements AbstractTask {
    private final Reader reader;

    public ExecuteProgramTask(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void run(final OutputStrategy outputStrategy) throws IOException {
    	ParseEntryPoint parser = new ParseEntryPoint();
    	parser.parseProgram(reader);
        if (parser.hasError()) {
            outputStrategy.error(parser.getError());
        }
        else {
            runProgram(parser, outputStrategy);
        }
    }

    private void runProgram(ParseEntryPoint parser, final OutputStrategy outputStrategy) {
        ExecutionContext executionContext = new ExecutionContext(new Scope(), outputStrategy);
        parser.getParsedNodeAsProgram().execute(executionContext);
    }
}
