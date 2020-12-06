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
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;

final class StatementGroupCalculation implements AstNodeExecution, NeedsExplicitStop {
    private final StatementGroup statementGroup;
    AstNodeExecutionState state = BEFORE;
    private List<AstNode> children;
    private int childIndex = 0;
    private boolean stopRequested = false;

    StatementGroupCalculation(StatementGroup statementGroup) {
        this.statementGroup = statementGroup;
        this.children = statementGroup.getChildren();
    }

    private StatementGroupCalculation(final StatementGroupCalculation orig) {
        this.statementGroup = orig.statementGroup;
        this.state = orig.state;
        this.children = new ArrayList<>(orig.children);
        this.childIndex = orig.childIndex;
        this.stopRequested = orig.stopRequested;
    }

    @Override
    public AstNode getAstNode() {
        return statementGroup;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext context) {
        if(state == AFTER) {
            return null;
        }
        if(state == BEFORE) {
            context.pushVariableFrame(StackFrameAccess.SHOW_PARENT);
        }
        state = RUNNING;
        if(!stopRequested && childIndex < children.size()) {
            return children.get(childIndex++);
        }
        done(context);
        return null;
    }

    private void done(ExecutionContext context) {
        context.popVariableFrame();
        state = AFTER;
    }

    @Override
    public void stopFunctionCall() {
        stopRequested = true;
    }

    @Override
    public AstNodeExecution fork() {
        return new StatementGroupCalculation(this);
    }
}
