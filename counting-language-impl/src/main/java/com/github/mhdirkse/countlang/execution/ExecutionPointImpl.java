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

import static com.github.mhdirkse.countlang.execution.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.execution.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.execution.AstNodeExecutionState.RUNNING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
class ExecutionPointImpl implements ExecutionPoint {
    private List<ExecutionPointNode> nodes;

    @EqualsAndHashCode
    private class Transition {
        private AstNodeExecutionState from;
        private AstNodeExecutionState to;
        
        Transition(AstNodeExecutionState from, AstNodeExecutionState to) {
            this.from = from;
            this.to = to;
        }
    }

    private Set<Transition> allowedTransitions = new HashSet<>(Arrays.asList(
            new Transition(RUNNING, RUNNING),
            new Transition(RUNNING, BEFORE),
            new Transition(RUNNING, AFTER)));

    ExecutionPointImpl(List<ExecutionPointNode> nodes) {
        this.nodes = new ArrayList<>();
        this.nodes.addAll(nodes);
    }

    @Override
    public boolean isValid() {
        AstNodeExecutionState currentState = RUNNING;
        for(ExecutionPointNode node: nodes) {
            AstNodeExecutionState nodeState = node.getState();
            if(! allowedTransitions.contains(new Transition(currentState, nodeState))) {
                return false;
            }
            currentState = nodeState;
        }
        return true;
    }

    @Override
    public ExecutionPoint afterFinished() {
        if(! isValid()) {
            throw new IllegalStateException("Invalid ExecutionPoint cannot produce afterFinished");
        }
        if(nodes.isEmpty()) {
            throw new IllegalStateException("Empty ExecutionPoint cannot produce afterFinished");
        }
        List<ExecutionPointNode> resultNodes = new ArrayList<>();
        for(int i = 0; i < (nodes.size() - 1); i++) {
            resultNodes.add(nodes.get(i));
        }
        resultNodes.add(new ExecutionPointNode(nodes.get(nodes.size() - 1).getNode(), AFTER));
        return new ExecutionPointImpl(resultNodes);
    }

    List<AstNodeExecutionState> getStates() {
        return nodes.stream().map(ExecutionPointNode::getState).collect(Collectors.toList());
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
