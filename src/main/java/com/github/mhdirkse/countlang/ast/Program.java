package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public final class Program extends AstNode {
    private List<Statement> statements = new ArrayList<Statement>();

    public Statement getStatement(final int index) {
        return statements.get(index);
    }

    public void addStatement(final Statement statement) {
        statements.add(statement);
    }

    public int getSize() {
        return statements.size();
    }

    public void execute(final ExecutionContext ctx) {
        for(Statement statement : statements) {
            statement.execute(ctx);
        }
    }

    @Override
    public void accept(final AstNode.Visitor v) {
        v.visitProgram(this);
    }
}
