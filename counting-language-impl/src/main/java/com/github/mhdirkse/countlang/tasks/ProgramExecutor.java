/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.mhdirkse.countlang.algorithm.OutputStrategy;
import com.github.mhdirkse.countlang.analysis.Analysis;
import com.github.mhdirkse.countlang.ast.FunctionDefinition;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.TestFunctionDefinitions;
import com.github.mhdirkse.countlang.execution.Stepper;
import com.github.mhdirkse.countlang.lang.parsing.ParseEntryPoint;
import com.github.mhdirkse.countlang.predef.ArrayAdd;
import com.github.mhdirkse.countlang.predef.ArrayAscending;
import com.github.mhdirkse.countlang.predef.ArrayDescending;
import com.github.mhdirkse.countlang.predef.ArrayUnsort;
import com.github.mhdirkse.countlang.predef.DistributionAscending;
import com.github.mhdirkse.countlang.predef.DistributionCountOf;
import com.github.mhdirkse.countlang.predef.DistributionDescending;
import com.github.mhdirkse.countlang.predef.DistributionFracE;
import com.github.mhdirkse.countlang.predef.DistributionFracSum;
import com.github.mhdirkse.countlang.predef.DistributionIntE;
import com.github.mhdirkse.countlang.predef.DistributionIntSum;
import com.github.mhdirkse.countlang.predef.DistributionKnown;
import com.github.mhdirkse.utils.Imperative;

public class ProgramExecutor {
    private final Reader reader;

    public ProgramExecutor(Reader reader) {
        this.reader = reader;
    }

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
        checks.add(() -> check(statementGroup, outputStrategy));
        Runnable runProgram = () -> runProgram(statementGroup, outputStrategy);
        Imperative.runWhileTrue(checks, runProgram);
    }

    private boolean check(final StatementGroup statementGroup, final OutputStrategy outputStrategy) {
        StatusReporter reporter = new StatusReporterImpl(outputStrategy);
        new Analysis(getPredefinedFunctions()).analyze(statementGroup, reporter);
        return ! reporter.hasErrors();
    }

    private List<FunctionDefinition> getPredefinedFunctions() {
        return Arrays.asList(new DistributionKnown(), new DistributionCountOf(),
        		new DistributionIntSum(), new DistributionFracSum(),
        		new DistributionIntE(), new DistributionFracE(),
        		new DistributionAscending(), new DistributionDescending(),
        		new ArrayAscending(), new ArrayDescending(),
        		new ArrayAdd(), new ArrayUnsort(),
        		TestFunctionDefinitions.createTestFunction());
    }

    private void runProgram(final StatementGroup statementGroup, final OutputStrategy outputStrategy) {
        Stepper stepper = Stepper.getInstance(statementGroup, outputStrategy, getPredefinedFunctions());
        try {
            stepper.run();
        } catch(ProgramException e) {
            outputStrategy.error(e.getMessage());
        }
    }

    public Stepper getStepper(final OutputStrategy outputStrategy) throws IOException {
        Optional<StatementGroup> statementGroup = parseProgram(outputStrategy);
        if(statementGroup.isPresent()) {
            return checkAndGetStepper(outputStrategy, statementGroup.get());
        }
        return null;
    }

    private Stepper checkAndGetStepper(final OutputStrategy outputStrategy, final StatementGroup statementGroup) throws IOException {
        if(! check(statementGroup, outputStrategy)) {
            return null;
        }
        return Stepper.getInstance(statementGroup, outputStrategy, getPredefinedFunctions());
    }
}
