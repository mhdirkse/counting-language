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

import java.util.EnumSet;
import java.util.stream.Stream;

import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

import lombok.Getter;

abstract class CodeBlockFunctionBase extends CodeBlockSerial {
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

    @Override
    boolean isRootOrFunction() {
        return true;
    }

    void report(StatusReporter reporter) {
        Stream.concat(Stream.of(this), getDescendants().stream().filter(b -> ! b.isRootOrFunction()))
            .forEach(b -> b.reportStatementHasNoEffect(reporter, functionKey));
        reportMissingReturn(reporter);
    }

    abstract void reportMissingReturn(StatusReporter reporter);

    static class Function extends CodeBlockFunctionBase {
        Function(CodeBlock parent, int line, int column, final FunctionKey functionKey) {
            super(parent, line, column, functionKey);
        }

        @Override
        void reportMissingReturn(StatusReporter reporter) {
            if(MISSING_RETURN.contains(getReturnStatus())) {
                reporter.report(StatusCode.FUNCTION_DOES_NOT_RETURN, line, column, functionKey.toString());
            }
        }
    }

    static class Experiment extends CodeBlockFunctionBase {
        Experiment(CodeBlock parent, int line, int column, final FunctionKey functionKey) {
            super(parent, line, column, functionKey);
        }

        @Override
        void reportMissingReturn(StatusReporter reporter) {
        }
    }
}
