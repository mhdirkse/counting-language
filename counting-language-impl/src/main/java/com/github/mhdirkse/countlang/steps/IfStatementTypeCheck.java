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
import com.github.mhdirkse.countlang.tasks.StatusCode;

class IfStatementTypeCheck extends ExpressionsAndStatementsCombinationHandler.TypeCheck {
    private final IfStatement ifStatement;
    private final SubExpressionStepper<CountlangType> selectorHandler;
    private final List<StatementGroup> statementGroups;
    private int statementGroupIndex = 0;
    private boolean inBranch = false;

    IfStatementTypeCheck(IfStatement ifStatement) {
        this.ifStatement = ifStatement;
        this.selectorHandler = new SubExpressionStepper<CountlangType>(ifStatement.getSubExpressions());
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
    public AstNode stepBefore(ExecutionContext<CountlangType> context) {
        setState(DOING_EXPRESSIONS);
        return selectorHandler.step(context);
    }

    @Override
    AstNode stepDoingExpressions(ExecutionContext<CountlangType> context) {
        if(!selectorHandler.getSubExpressionResults().get(0).equals(CountlangType.BOOL)) {
            reportSelectorNotBoolean((ExpressionNode) ifStatement.getSelector(), context);
        }
        setState(DOING_STATEMENTS);
        context.onSwitchOpened();
        return newBranch(context);
    }

    private void reportSelectorNotBoolean(ExpressionNode selector, ExecutionContext<CountlangType> context) {
        context.report(
                StatusCode.IF_SELECT_NOT_BOOLEAN,
                selector.getLine(),
                selector.getColumn(),
                selector.getCountlangType().toString());

    }

    private AstNode newBranch(ExecutionContext<CountlangType> context) {
        ensureBranchClosed(context);
        context.onBranchOpened();
        inBranch = true;
        return statementGroups.get(statementGroupIndex++);                
    }

    private void ensureBranchClosed(ExecutionContext<CountlangType> context) {
        if(inBranch) {
            context.onBranchClosed();
            inBranch = false;
        }
    }

    @Override
    AstNode stepDoingStatements(ExecutionContext<CountlangType> context) {
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
    boolean handleDescendantResultDoingExpressions(CountlangType value) {
        selectorHandler.handleDescendantResult(value);
        return true;        
    }

    @Override
    boolean handleDescendantResultDoingStatements(CountlangType value) {
        return false;        
    }    
}
