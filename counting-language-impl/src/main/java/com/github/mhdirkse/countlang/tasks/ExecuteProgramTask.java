package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.TestFunctionDefinitions;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
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
        checks.add(() -> typeCheck(statementGroup, outputStrategy));
        checks.add(() -> checkVariables(statementGroup, outputStrategy));
        Runnable runProgram = () -> runProgramVisitor(statementGroup, outputStrategy);
        Imperative.runWhileTrue(checks, runProgram);
    }

    private boolean typeCheck(final StatementGroup statementGroup, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        statementGroup.accept(TypeCheck.getInstance(reporter, getPredefinedFunctions()));
        return !reporter.hasErrors();
    }

    private List<FunctionDefinitionStatement> getPredefinedFunctions() {
        return Arrays.asList(TestFunctionDefinitions.createTestFunction());
    }

    private boolean checkVariables(final StatementGroup statementGroup, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        VariableCheck check = VariableCheck.getInstance(reporter, getPredefinedFunctions());
        statementGroup.accept(check);
        check.listEvents();
        return !reporter.hasErrors();
    }

    private void runProgramVisitor(final StatementGroup statementGroup, final OutputStrategy outputStrategy) {
        CountlangRunner runner = CountlangRunner.getInstance(
                outputStrategy, getPredefinedFunctions());
        try {
            statementGroup.accept(runner);
        }
        catch (ProgramException e) {
            outputStrategy.error(e.getMessage());
        }
    }
}
