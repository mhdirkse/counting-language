package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.TestFunctionDefinitions;
import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ExecutionContextImpl;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.lang.parsing.ParseEntryPoint;
import com.github.mhdirkse.utils.Imperative;

public class ExecuteProgramTask implements AbstractTask {
    private final Reader reader;

    public ExecuteProgramTask(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void run(final OutputStrategy outputStrategy) throws IOException {
        Optional<Program> program = parseProgram(outputStrategy);
        if(program.isPresent()) {
            checkAndRunProgram(outputStrategy, program.get());
        }
    }

    Optional<Program> parseProgram(final OutputStrategy outputStrategy) throws IOException {
        ParseEntryPoint parser = new ParseEntryPoint();
        parser.parseProgram(reader);
        if (parser.hasError()) {
            outputStrategy.error(parser.getError());
            return Optional.<Program>empty();
        } else {
            return Optional.of(parser.getParsedNodeAsProgram());
        }
    }

    private void checkAndRunProgram(final OutputStrategy outputStrategy, final Program program) throws IOException {
        List<Supplier<Boolean>> checks = new ArrayList<>();
        checks.add(() -> checkFunctionsAndReturns(program, outputStrategy));
        checks.add(() -> checkVariables(program, outputStrategy));
        checks.add(() -> checkFunctionCalls(program, outputStrategy));
        checks.add(() -> typeCheck(program, outputStrategy));
        Runnable runProgram = () -> runProgram(program, outputStrategy);
        Imperative.runWhileTrue(checks, runProgram);
    }

    private boolean checkFunctionsAndReturns(final Program program, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        new FunctionAndReturnCheck(program, reporter).run();
        return !reporter.hasErrors();
    }

    private boolean checkVariables(final Program program, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        new VariableCheck(reporter).run(program);
        return !reporter.hasErrors();
    }

    private boolean checkFunctionCalls(final Program program, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        new FunctionCallCheck(reporter).run(program);
        return !reporter.hasErrors();
    }

    private boolean typeCheck(final Program program, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        new TypeCheck(reporter, program).run();
        return !reporter.hasErrors();
    }

    private void runProgram(final Program program, final OutputStrategy outputStrategy) {
        try {
            ExecutionContext executionContext = new ExecutionContextImpl(outputStrategy);
            executionContext.putFunction(TestFunctionDefinitions.createTestFunction());
            program.execute(executionContext);
        }
        catch (ProgramException e) {
            outputStrategy.error(e.getMessage());
        }
    }
}
