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

package com.github.mhdirkse.countlang.execution;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.type.CountlangTuple;
import com.github.mhdirkse.countlang.utils.Utils;

final class PrintStatementCalculation extends ExpressionResultsCollector {
    PrintStatementCalculation(PrintStatement node) {
        super(node);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        Object value = subExpressionResults.get(0);
        String output = null;
        if(value instanceof Distribution) {
            output = ((Distribution) value).format();
        } else if(value instanceof CountlangTuple) {
            output = ((CountlangTuple) value).listMembers();
        } else {
            output = Utils.genericFormat(value);
        }
        context.output(output);
    }
}
