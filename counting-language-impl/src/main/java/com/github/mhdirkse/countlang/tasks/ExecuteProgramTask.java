package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.Reader;

import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.TestFunctionDefinitions;
import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ExecutionContextImpl;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.lang.parsing.ParseEntryPoint;

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
        else if(checkFunctionsAndReturns(parser.getParsedNodeAsProgram(), outputStrategy)) {
            runProgram(parser, outputStrategy);
        }
    }

    private boolean checkFunctionsAndReturns(final Program program, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        new FunctionAndReturnCheck(program, reporter).run();
        return !reporter.hasErrors();
    }

    private void runProgram(ParseEntryPoint parser, final OutputStrategy outputStrategy) {
        try {
            ExecutionContext executionContext = new ExecutionContextImpl(outputStrategy);
            executionContext.putFunction(TestFunctionDefinitions.createTestFunction());
            parser.getParsedNodeAsProgram().execute(executionContext);
        }
        catch (ProgramException e) {
            outputStrategy.error(e.getMessage());
        }
    }
}
