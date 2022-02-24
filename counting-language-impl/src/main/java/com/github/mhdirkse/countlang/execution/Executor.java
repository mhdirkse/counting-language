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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.algorithm.SampleContext;
import com.github.mhdirkse.countlang.ast.AbstractDistributionItem;
import com.github.mhdirkse.countlang.ast.AbstractSampleStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.Call;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;

class Executor {
    private final Deque<AstNodeExecution> callStack;
    private final ExecutionContext context;
    private final AstNodeExecutionFactory factory;

    Executor(final ExecutionContext context, final AstNodeExecutionFactory factory, AstNodeExecution firstExecution) {
        this.factory = factory;
        this.context = context;
        callStack = new ArrayDeque<>();
        callStack.addLast(firstExecution);
    }

    Executor(Executor orig) {
        this.factory = orig.factory;
        this.context = new ExecutionContextCalculate((ExecutionContextCalculate) orig.context);
        this.callStack = forkCallStack(orig.callStack);
    }

    private static Deque<AstNodeExecution> forkCallStack(Deque<AstNodeExecution> orig) {
        Deque<AstNodeExecution> result = new ArrayDeque<>();
        boolean toFork = true;
        Iterator<AstNodeExecution> it = orig.descendingIterator();
        while(it.hasNext()) {
            AstNodeExecution origItem = it.next();
            AstNodeExecution toAdd = null;
            if(toFork) {
                toAdd = origItem.fork();
            } else {
                toAdd = origItem;
            }
            result.addFirst(toAdd);
            if(origItem instanceof FunctionCallExpressionCalculation) {
                toFork = false;
            }
        }
        return result;
    }

    boolean hasMoreSteps() {
        return !callStack.isEmpty();
    }

    void step() {
        if(callStack.getLast().getState() == AFTER) {
            callStack.removeLast();
        }
        if(!callStack.isEmpty()) {
            AstNode child = callStack.getLast().step(context);
            if(child != null) {
                AstNodeExecution newNodeExecution = factory.create(child, getContext(child));
                callStack.addLast(newNodeExecution);
            }
        }
    }

    private Object getContext(AstNode child) {
        Object context = null;
        context = getContextForDistributionItem(child);
        if(context == null) {
            context = getContextForSampleStatement(child);
        }
        return context;
    }

    private Object getContextForDistributionItem(AstNode child) {
        Distribution.Builder result = null;
        if(child instanceof AbstractDistributionItem) {
            AstNodeExecution rawParent = callStack.getLast();
            if(! (rawParent instanceof SimpleDistributionExpressionCalculation)) {
                throw new IllegalStateException("Distribution items can only occur in distribution literals");
            }
            SimpleDistributionExpressionCalculation parent = (SimpleDistributionExpressionCalculation) rawParent;
            result = parent.getDistributionBuilder();
        }
        return result;
    }

    private SampleContext getContextForSampleStatement(AstNode child) {
        if(child instanceof AbstractSampleStatement) {
            Iterator<AstNodeExecution> it = callStack.descendingIterator();
            while(it.hasNext()) {
                AstNodeExecution calculation = it.next();
                if(calculation.getAstNode() instanceof FunctionCallExpression) {
                    return ((FunctionCallExpressionCalculation) calculation).getSampleContext();
                }
            }
            throw new IllegalStateException("Sample statement encountered outside experiment");
        }
        return null;
    }

    Object onResult(Object value) {
        Iterator<AstNodeExecution> it = callStack.descendingIterator();
        it.next();
        nextChildAcceptor(it).ifPresent(acc -> acc.acceptChildResult(value, context));
        return value;
    }

    private Optional<AstNodeExecution> nextChildAcceptor(Iterator<AstNodeExecution> it) {
        while(it.hasNext()) {
            AstNodeExecution current = it.next();
            if(current.isAcceptingChildResults()) {
                return Optional.of(current);
            }
        }
        return Optional.empty();
    }

    void stopFunctionCall() {
        Iterator<AstNodeExecution> it = callStack.descendingIterator();
        AstNodeExecution currentExecution = it.next();
        // We do not check on reaching the end of the callStack, but on finding the function we want to stop.
        while(! (currentExecution.getAstNode() instanceof Call)) {
            if(currentExecution instanceof NeedsExplicitStop) {
                ((NeedsExplicitStop) currentExecution).stopFunctionCall();
            }
            currentExecution = it.next();
        }
    }

    ExecutionPoint getExecutionPoint() {
        List<ExecutionPointNode> nodes = new ArrayList<>();
        callStack.forEach(node -> nodes.add(node.getExecutionPointNode()));
        return new ExecutionPointImpl(nodes);
    }
}
