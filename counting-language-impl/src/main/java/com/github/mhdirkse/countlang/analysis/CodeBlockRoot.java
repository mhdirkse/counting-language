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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

class CodeBlockRoot extends CodeBlockSerial implements RootOrFunctionCodeBlock {
    CodeBlockRoot() {
        super();
    }

    void report(final StatusReporter reporter) {
        getAllVariableWrites().forEach(w -> w.report(reporter));
        if(getReturnStatus() != ReturnStatus.NONE_RETURN) {
            CodeBlockEvent.Return offending = Stream.concat(
                    getReturnStatements().stream(),
                    getNonSubfunctionChildren().stream().flatMap(b -> b.getReturnStatements().stream()))
                    .collect(Collectors.minBy(CodeBlockEvent.Return.COMPARATOR)).get();
            reporter.report(StatusCode.RETURN_OUTSIDE_FUNCTION, offending.getLine(), offending.getColumn());
        }
        getDescendants().stream()
            .filter(b -> b instanceof CodeBlockFunctionBase)
            .map(b -> (CodeBlockFunctionBase) b)
            .forEach(b -> b.report(reporter));
    }

    @Override
    public void checkReturnStatementType(int line, int column, Class<? extends Statement> returnStatementType) {
    	// Nothing to do. No return statement whatsoever is allowed.
    }

    List<VariableWrite> getAllVariableWrites() {
        return Stream.concat(getVariableWrites().stream(), getDescendants().stream().flatMap(b -> b.getVariableWrites().stream()))
                .collect(Collectors.toList());
    }
}
