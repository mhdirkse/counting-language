package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

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
