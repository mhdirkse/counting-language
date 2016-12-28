package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public final class Program {
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
}
