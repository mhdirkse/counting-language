package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.mhdirkse.countlang.ast.StatementGroup;
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
        Optional<StatementGroup> statementGroup = parseProgram(outputStrategy);
        if(statementGroup.isPresent()) {
            checkAndRunProgram(outputStrategy, statementGroup.get());
        }
    }

    Optional<StatementGroup> parseProgram(final OutputStrategy outputStrategy) throws IOException {
        ParseEntryPoint parser = new ParseEntryPoint();
        parser.parseProgram(reader);
        if (parser.hasError()) {
            outputStrategy.error(parser.getError());
            return Optional.<StatementGroup>empty();
        } else {
            return Optional.of(parser.getParsedNodeAsStatementGroup());
        }
    }

    private void checkAndRunProgram(final OutputStrategy outputStrategy, final StatementGroup statementGroup) throws IOException {
        List<Supplier<Boolean>> checks = new ArrayList<>();
        checks.add(() -> checkFunctionsAndReturns(statementGroup, outputStrategy));
        checks.add(() -> checkVariables(statementGroup, outputStrategy));
        checks.add(() -> checkFunctionCalls(statementGroup, outputStrategy));
        checks.add(() -> typeCheck(statementGroup, outputStrategy));
        Runnable runProgram = () -> runProgram(statementGroup, outputStrategy);
        Imperative.runWhileTrue(checks, runProgram);
    }

    private boolean checkFunctionsAndReturns(final StatementGroup statementGroup, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        new FunctionAndReturnCheck(statementGroup, reporter).run();
        return !reporter.hasErrors();
    }

    private boolean checkVariables(final StatementGroup statementGroup, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        new VariableCheck(reporter).run(statementGroup);
        return !reporter.hasErrors();
    }

    private boolean checkFunctionCalls(final StatementGroup statementGroup, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        new FunctionCallCheck(reporter).run(statementGroup);
        return !reporter.hasErrors();
    }

    private boolean typeCheck(final StatementGroup statementGroup, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        new TypeCheck(reporter, statementGroup).run();
        return !reporter.hasErrors();
    }

    private void runProgram(final StatementGroup statementGroup, final OutputStrategy outputStrategy) {
        try {
            ExecutionContext executionContext = new ExecutionContextImpl(outputStrategy);
            executionContext.putFunction(TestFunctionDefinitions.createTestFunction());
            statementGroup.execute(executionContext);
        }
        catch (ProgramException e) {
            outputStrategy.error(e.getMessage());
        }
    }
}
