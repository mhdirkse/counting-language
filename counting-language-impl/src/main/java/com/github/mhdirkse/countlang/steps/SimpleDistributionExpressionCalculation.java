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

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import com.github.mhdirkse.countlang.ast.AbstractDistributionExpression;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.types.Distribution;

class SimpleDistributionExpressionCalculation implements AstNodeExecution {
    private AstNodeExecutionState state = BEFORE;
    private Distribution.Builder builder = new Distribution.Builder();
    AbstractDistributionExpression expression;
    int indexInItems = 0;

    SimpleDistributionExpressionCalculation(AbstractDistributionExpression expression) {
        this.expression = expression;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode getAstNode() {
        return expression;
    }

    @Override
    public AstNode step(ExecutionContext context) {
        if(state == AFTER) {
            return null;
        }
        state = RUNNING;
        if(indexInItems < expression.getChildren().size()) {
            return expression.getChildren().get(indexInItems++);    
        }
        state = AFTER;
        finishBuilder();
        context.onResult(builder.build());
        return null;
    }

    void finishBuilder() {
    }

    @Override
    public Object getContext() {
        return builder;
    }
}
