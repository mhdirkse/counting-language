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

package com.github.mhdirkse.countlang.execution;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.DistributionItemCount;
import com.github.mhdirkse.countlang.ast.ProgramException;

class DistributionItemCountCalculation extends ExpressionResultsCollector {
    private final Distribution.Builder builder;

    DistributionItemCountCalculation(DistributionItemCount node, Distribution.Builder builder) {
        super(node);
        this.builder = builder;
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        int count = (Integer) subExpressionResults.get(0);
        Object item = subExpressionResults.get(1);
        if(count < 0) {
            throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(),
                    String.format("Item is added to distribution with negative count %d", count));
        }
        builder.add(item, count);
    }
}
