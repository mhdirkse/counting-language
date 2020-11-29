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

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ExpressionNode;

class SubExpressionStepper {
    private List<ExpressionNode> subExpressions = null;
    private AstNodeExecutionState state;
    private int subExpressionIndex = 0;
    private List<Object> subExpressionResults = new ArrayList<>();

    SubExpressionStepper(final List<ExpressionNode> subExpressions) {
        this.subExpressions = subExpressions;
        this.state = BEFORE;
    }

    SubExpressionStepper(final SubExpressionStepper orig) {
        subExpressions = new ArrayList<>(orig.subExpressionIndex);
        this.state = orig.state;
        this.subExpressionIndex = orig.subExpressionIndex;
        subExpressionResults = new ArrayList<>(orig.subExpressionResults);
    }

    public AstNodeExecutionState getState() {
        return state;
    }

    public boolean isDone() {
        return state.equals(AFTER);
    }

    public AstNode step(ExecutionContext context) {
        if(state == AFTER) {
            return null;
        }
        state = RUNNING;
        if(subExpressionIndex < subExpressions.size()) {
            return subExpressions.get(subExpressionIndex++);
        }
        else {
            state = AFTER;
            return null;
        }
    }

    public void acceptChildResult(Object value) {
        subExpressionResults.add(value);
    }

    public List<Object> getSubExpressionResults() {
        return subExpressionResults;
    }
}
