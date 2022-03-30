/*
 * Copyright Martijn Dirkse 2022
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

import static com.github.mhdirkse.countlang.execution.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.execution.AstNodeExecutionState.BEFORE;

import com.github.mhdirkse.countlang.ast.AstNode;

abstract class AbstractSampleStatementCalculationForked implements AstNodeExecution {
    final Object value;
    final AstNode sampleStatement;
    private AstNodeExecutionState state = BEFORE;

    AbstractSampleStatementCalculationForked(AbstractSampleStatementCalculation orig) {
        this.value = orig.value;
        this.sampleStatement = orig.getAstNode();
    }

    @Override
    public AstNode getAstNode() {
        return sampleStatement;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext context) {
        if(state == BEFORE) {
            assign(context);
        	state = AFTER;
        }
        return null;
    }

    abstract void assign(ExecutionContext context);
}
