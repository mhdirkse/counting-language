package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.StatementGroup;

class FunctionDefinitionStatementHandler extends FunctionDefinitionStatementHandlerBase
implements StatementSource {
    private FunctionDefinitionStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    @Override
    void addStatementGroup(StatementGroup statements) {
        statement.setStatements(statements);
    }

    @Override
    public void setText(final String text) {
        statement.setName(text);
    }

    FunctionDefinitionStatementHandler(final int line, final int column) {
        super(line, column);
        statement = new FunctionDefinitionStatement(line, column);
    }

    @Override
    void addFormalParameter(String name, CountlangType countlangType) {
        statement.addFormalParameter(name, countlangType);
    }
}
