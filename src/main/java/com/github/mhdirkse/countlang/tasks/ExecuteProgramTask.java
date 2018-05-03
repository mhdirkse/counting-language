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
    public void run(final OutputContext outputContext) throws IOException {
        ParseEntryPoint.getProgram(reader).execute(getExecutionContext(outputContext));
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
