package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public final class StatementGroup extends Statement implements CompositeNode {

    private final StackStrategy stackStrategy;

    private List<Statement> statements = new ArrayList<Statement>();

    public StatementGroup(final StackStrategy stackStrategy, final int line, final int column) {
        super(line, column);
        this.stackStrategy = stackStrategy;
    }

    public Statement getStatement(final int index) {
        return statements.get(index);
    }

    public void addStatement(final Statement statement) {
        statements.add(statement);
    }

    public int getSize() {
        return statements.size();
    }

    public StackStrategy getStackStrategy() {
        return stackStrategy;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitStatementGroup(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.addAll(statements);
        return result;
    }
}
