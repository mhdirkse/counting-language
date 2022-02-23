package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.ProcedureDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.TypeNode;

class ProcedureDefinitionStatementHandler extends FunctionDefinitionStatementHandlerBase
implements StatementSource {
	private ProcedureDefinitionStatement statement;

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
        statement.setKey(new FunctionKey(text));
    }

    ProcedureDefinitionStatementHandler(final int line, final int column) {
        super(line, column);
        statement = new ProcedureDefinitionStatement(line, column);
    }

    @Override
    void addFormalParameter(String name, TypeNode typeNode) {
        statement.addFormalParameter(name, typeNode);
    }
}
