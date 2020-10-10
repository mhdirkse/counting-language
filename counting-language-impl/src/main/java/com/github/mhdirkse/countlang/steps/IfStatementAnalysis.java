package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DONE;

import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.execution.DummyValue;
import com.github.mhdirkse.countlang.tasks.StatusCode;

abstract class IfStatementAnalysis<T> extends ExpressionsAndStatementsCombinationHandler<T> {
    final IfStatement ifStatement;
    final SubExpressionStepper<T> selectorHandler;
    private final List<StatementGroup> statementGroups;
    private int statementGroupIndex = 0;
    private boolean inBranch = false;

    IfStatementAnalysis(IfStatement ifStatement) {
        this.ifStatement = ifStatement;
        this.selectorHandler = new SubExpressionStepper<T>(ifStatement.getSubExpressions());
        List<AstNode> childNodes = ifStatement.getChildren();
        this.statementGroups = childNodes.subList(1, childNodes.size()).stream()
                .map(c -> (StatementGroup) c)
                .collect(Collectors.toList());
    }

    @Override
    public AstNode getAstNode() {
        return ifStatement;
    }

    @Override
    public AstNode stepBefore(ExecutionContext<T> context) {
        setState(DOING_EXPRESSIONS);
        return selectorHandler.step(context);
    }

    @Override
    AstNode stepDoingExpressions(ExecutionContext<T> context) {
        afterSelector(context);
        setState(DOING_STATEMENTS);
        context.onSwitchOpened();
        return newBranch(context);
    }

    abstract void afterSelector(ExecutionContext<T> context);
    
    private AstNode newBranch(ExecutionContext<T> context) {
        ensureBranchClosed(context);
        context.onBranchOpened();
        inBranch = true;
        return statementGroups.get(statementGroupIndex++);                
    }

    private void ensureBranchClosed(ExecutionContext<T> context) {
        if(inBranch) {
            context.onBranchClosed();
            inBranch = false;
        }
    }

    @Override
    AstNode stepDoingStatements(ExecutionContext<T> context) {
        if(statementGroupIndex < statementGroups.size()) {
            return newBranch(context);
        }
        else {
            ensureBranchClosed(context);
            context.onSwitchClosed();
            setState(DONE);
        }
        return null;        
    }

    @Override
    boolean handleDescendantResultDoingExpressions(T value) {
        selectorHandler.handleDescendantResult(value);
        return true;        
    }

    @Override
    boolean handleDescendantResultDoingStatements(T value) {
        return false;        
    }

    static class TypeCheck extends IfStatementAnalysis<CountlangType> {
        TypeCheck(final IfStatement statement) {
            super(statement);
        }

        void afterSelector(final ExecutionContext<CountlangType> context) {
            if(!selectorHandler.getSubExpressionResults().get(0).equals(CountlangType.BOOL)) {
                reportSelectorNotBoolean((ExpressionNode) ifStatement.getSelector(), context);
            }
        }

        private void reportSelectorNotBoolean(ExpressionNode selector, ExecutionContext<CountlangType> context) {
            context.report(
                    StatusCode.IF_SELECT_NOT_BOOLEAN,
                    selector.getLine(),
                    selector.getColumn(),
                    selector.getCountlangType().toString());

        }
    }

    static class VarUsage extends IfStatementAnalysis<DummyValue> {
        VarUsage(final IfStatement statement) {
            super(statement);
        }

        @Override
        void afterSelector(ExecutionContext<DummyValue> context) {
        }
    }
}
