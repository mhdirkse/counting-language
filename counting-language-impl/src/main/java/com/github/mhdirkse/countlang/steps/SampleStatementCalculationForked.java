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

package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;

import com.github.mhdirkse.countlang.ast.AstNode;

class SampleStatementCalculationForked implements AstNodeExecution {
    private final String symbol;
    private final Object value;
    private final AstNode node;
    private AstNodeExecutionState state = BEFORE;

    SampleStatementCalculationForked(SampleStatementCalculation orig) {
        this.symbol = orig.statement.getSymbol();
        this.value = orig.value;
        this.node = orig.getAstNode();
    }

    @Override
    public AstNode getAstNode() {
        return node;
    }


    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext context) {
        if(state == BEFORE) {
            context.writeSymbol(symbol, value, node);
            state = AFTER;
        }
        return null;
    }
}
