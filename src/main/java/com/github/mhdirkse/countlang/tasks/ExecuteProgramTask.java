package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.Reader;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.ProgramRuntimeException;
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
        try {
            ExecutionContext executionContext = new ExecutionContext(outputStrategy);
            parser.getParsedNodeAsProgram().execute(executionContext);
        }
        catch (ProgramRuntimeException e) {
            outputStrategy.error(e.getMessage());
        }
    }
}
