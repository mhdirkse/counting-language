/*
 * Copyright Martijn Dirkse 2021
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

package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.NonValueReturnStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.ValueReturnStatement;
import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

import lombok.Getter;

abstract class CodeBlockFunctionBase extends CodeBlockSerial implements RootOrFunctionCodeBlock {
    private static final EnumSet<ReturnStatus> MISSING_RETURN = EnumSet.<ReturnStatus>of(ReturnStatus.NONE_RETURN, ReturnStatus.SOME_RETURN);
    int line;
    int column;
    final @Getter FunctionKey functionKey;

    CodeBlockFunctionBase(CodeBlock parent, int line, int column, final FunctionKey functionKey) {
        super(parent);
        this.functionKey = functionKey;
        this.line = line;
        this.column = column;
    }

    void report(StatusReporter reporter) {
        Stream.concat(Stream.of(this), getDescendants().stream().filter(b -> ! (b instanceof RootOrFunctionCodeBlock)))
            .forEach(b -> b.reportStatementHasNoEffect(reporter, functionKey));
        reportMissingReturn(reporter);
        reportInvalidReturnType(reporter);
    }

    abstract void reportMissingReturn(StatusReporter reporter);
    abstract void reportInvalidReturnType(StatusReporter reporter);

    static class Function extends CodeBlockFunctionBase {
        private final List<InvalidReturnTypeEvent> offending = new ArrayList<>();

    	Function(CodeBlock parent, int line, int column, final FunctionKey functionKey) {
            super(parent, line, column, functionKey);
        }

        @Override
        void reportMissingReturn(StatusReporter reporter) {
            if(MISSING_RETURN.contains(getReturnStatus())) {
                reporter.report(StatusCode.FUNCTION_DOES_NOT_RETURN, line, column, functionKey.toString());
            }
        }

        @Override
        public void checkReturnStatementType(int line, int column, Class<? extends Statement> returnStatementType) {
        	if(returnStatementType.isAssignableFrom(NonValueReturnStatement.class)) {
        		offending.add(new InvalidReturnTypeEvent(line, column));
        	}
        }

        @Override
        void reportInvalidReturnType(StatusReporter reporter) {
        	offending.forEach(e -> reporter.report(StatusCode.FUNCTION_SHOULD_RETURN_VALUE, e.line, e.column, functionKey.toString()));
        }
    }

    static class Experiment extends CodeBlockFunctionBase {
        Experiment(CodeBlock parent, int line, int column, final FunctionKey functionKey) {
            super(parent, line, column, functionKey);
        }

        @Override
        void reportMissingReturn(StatusReporter reporter) {
        }

        @Override
        public void checkReturnStatementType(int line, int column, Class<? extends Statement> returnStatementType) {
        }

        @Override
        public void reportInvalidReturnType(StatusReporter reporter) {
        }
    }

    static class Procedure extends CodeBlockFunctionBase {
        private final List<InvalidReturnTypeEvent> offending = new ArrayList<>();

    	Procedure(CodeBlock parent, int line, int column, final FunctionKey functionKey) {
            super(parent, line, column, functionKey);
        }

        @Override
        void reportMissingReturn(StatusReporter reporter) {
        }

        @Override
        public void checkReturnStatementType(int line, int column, Class<? extends Statement> returnStatementType) {
        	if(returnStatementType.isAssignableFrom(ValueReturnStatement.class)) {
        		offending.add(new InvalidReturnTypeEvent(line, column));
        	}
        }

        @Override
        public void reportInvalidReturnType(StatusReporter reporter) {
        	offending.forEach(e -> reporter.report(StatusCode.PROCEDURE_SHOULD_NOT_RETURN_VALUE, e.line, e.column, functionKey.toString()));
        }
    }
}
