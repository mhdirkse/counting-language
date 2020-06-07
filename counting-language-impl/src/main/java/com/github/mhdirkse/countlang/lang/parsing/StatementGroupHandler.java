package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.Statement;

class StatementGroupHandler extends AbstractStatementGroupHandler {
    private StatementGroup statementGroup;

    StatementGroup getStatementGroup() {
        return statementGroup;
    }

    StatementGroupHandler(final int line, final int column) {
        statementGroup = new StatementGroup(line, column);
    }

    @Override
    void addStatement(final Statement statement) {
        statementGroup.addStatement(statement);
    }
}
