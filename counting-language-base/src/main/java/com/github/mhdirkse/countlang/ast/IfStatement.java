package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

public class IfStatement extends Statement implements CompositeNode {
    private ExpressionNode selector;
    private StatementGroup thenStatement;
    private StatementGroup elseStatement;

    public IfStatement(final int line, final int column) {
        super(line, column);
    }
    
    public void setSelector(@NotNull final ExpressionNode selector) {
        if(selector == null) {
            throw new NullPointerException("Selector of if-statement should not be null");
        }
        this.selector = selector;
    }

    public ExpressionNode getSelector() {
        return selector;
    }

    public void setThenStatement(@NotNull final StatementGroup thenStatement) {
        if(thenStatement == null) {
            throw new NullPointerException("Then statement of if-statement should not be null");
        }
        this.thenStatement = thenStatement;
    }

    public StatementGroup getThenStatement() {
        return thenStatement;
    }

    public void setElseStatement(final StatementGroup elseStatement) {
        this.elseStatement = elseStatement;
    }

    public StatementGroup getElseStatement() {
        return elseStatement;
    }

    @Override
    public void accept(Visitor v) {
        v.visitIfStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>(3);
        result.add(selector);
        result.add(thenStatement);
        if(elseStatement != null) {
            result.add(elseStatement);
        }
        return result;
    }
}
