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
import com.github.mhdirkse.countlang.predef.ArrayAddAll;
import com.github.mhdirkse.countlang.predef.ArrayAscending;
import com.github.mhdirkse.countlang.predef.ArrayAscendingIndicesOf;
import com.github.mhdirkse.countlang.predef.ArrayDescending;
import com.github.mhdirkse.countlang.predef.ArrayMax;
import com.github.mhdirkse.countlang.predef.ArrayMaxRef;
import com.github.mhdirkse.countlang.predef.ArrayMin;
import com.github.mhdirkse.countlang.predef.ArrayMinRef;
import com.github.mhdirkse.countlang.predef.ArrayReverse;
import com.github.mhdirkse.countlang.predef.ArraySize;
import com.github.mhdirkse.countlang.predef.ArrayUnsort;
import com.github.mhdirkse.countlang.predef.DistributionAddAll;
import com.github.mhdirkse.countlang.predef.DistributionAscending;
import com.github.mhdirkse.countlang.predef.DistributionContains;
import com.github.mhdirkse.countlang.predef.DistributionCountOf;
import com.github.mhdirkse.countlang.predef.DistributionCountOfUnknown;
import com.github.mhdirkse.countlang.predef.DistributionDescending;
import com.github.mhdirkse.countlang.predef.DistributionFracE;
import com.github.mhdirkse.countlang.predef.DistributionFracSum;
import com.github.mhdirkse.countlang.predef.DistributionHasElement;
import com.github.mhdirkse.countlang.predef.DistributionHasUnknown;
import com.github.mhdirkse.countlang.predef.DistributionIntE;
import com.github.mhdirkse.countlang.predef.DistributionIntSum;
import com.github.mhdirkse.countlang.predef.DistributionIntersect;
import com.github.mhdirkse.countlang.predef.DistributionIsSet;
import com.github.mhdirkse.countlang.predef.DistributionKnown;
import com.github.mhdirkse.countlang.predef.DistributionProbabilityOf;
import com.github.mhdirkse.countlang.predef.DistributionProbabilityOfSet;
import com.github.mhdirkse.countlang.predef.DistributionProbabilityOfUnknown;
import com.github.mhdirkse.countlang.predef.DistributionRemoveAll;
import com.github.mhdirkse.countlang.predef.DistributionSize;
import com.github.mhdirkse.countlang.predef.DistributionToSet;
import com.github.mhdirkse.countlang.predef.FractionDenominator;
import com.github.mhdirkse.countlang.predef.FractionIsWhole;
import com.github.mhdirkse.countlang.predef.FractionNumerator;
import com.github.mhdirkse.countlang.predef.FractionProperWhole;
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
        return Arrays.asList(
        		new FractionProperWhole(), new FractionNumerator(),
        		new FractionDenominator(), new FractionIsWhole(),
        		new DistributionKnown(), new DistributionCountOf(),
        		new DistributionIntSum(), new DistributionFracSum(),
        		new DistributionIntE(), new DistributionFracE(),
        		new DistributionAscending(), new DistributionDescending(),
        		new DistributionSize(), new DistributionCountOfUnknown(),
        		new DistributionProbabilityOf(), new DistributionProbabilityOfUnknown(),
        		new DistributionAddAll(), new DistributionHasElement(),
        		new DistributionHasUnknown(), new DistributionContains(),
        		new DistributionIntersect(), new DistributionRemoveAll(),
        		new DistributionIsSet(), new DistributionToSet(),
        		new DistributionProbabilityOfSet(),
        		new ArrayAscending(), new ArrayDescending(),
        		new ArrayAdd(), new ArrayUnsort(),
        		new ArraySize(), new ArrayReverse(),
        		new ArrayAscendingIndicesOf(), new ArrayAddAll(),
        		new ArrayMin(), new ArrayMinRef(),
        		new ArrayMax(), new ArrayMaxRef(),
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
