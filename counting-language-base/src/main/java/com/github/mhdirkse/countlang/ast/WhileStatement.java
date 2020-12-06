package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

public class WhileStatement extends Statement implements CompositeNode {
    private ExpressionNode testExpr;
    private StatementGroup statement;

    public WhileStatement(int line, int column) {
        super(line, column);
    }

    public void setTestExpr(@NotNull final ExpressionNode testExpr) {
        if(testExpr == null) {
            throw new NullPointerException("Test expression of while-statement should not be null");
        }
        this.testExpr = testExpr;
    }

    public ExpressionNode getTestExpr() {
        return testExpr;
    }

    public void setStatement(@NotNull final StatementGroup statement) {
        if(statement == null) {
            throw new NullPointerException("Statement group of while-statement should not be null");
        }
        this.statement = statement;
    }

    public StatementGroup getStatement() {
        return statement;
    }

    @Override
    public void accept(Visitor v) {
        v.visitWhileStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(testExpr);
        result.add(statement);
        return result;
    }
}
