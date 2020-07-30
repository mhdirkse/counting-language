package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class IfStatementHandler extends AbstractExpressionHandler implements StatementSource {
    private IfStatement statement;
    
    IfStatementHandler(final int line, final int column) {
        super();
        statement = new IfStatement(line, column);
    }

    @Override
    public Statement getStatement() {
        return statement;
    }

    @Override
    void addExpression(ExpressionNode expression) {
        statement.setSelector(expression);
    }

    @Override
    public boolean enterStatements(
            @NotNull CountlangParser.StatementsContext ctx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new StatementGroupHandler(line, column));
        return true;   
    }

    @Override
    public boolean exitStatements(
            @NotNull CountlangParser.StatementsContext ctx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(delegationCtx.isFirst()) {
            return false;
        }
        StatementGroup group = ((StatementGroupHandler) delegationCtx.getPreviousHandler()).getStatementGroup();
        if(statement.getThenStatement() == null) {
            statement.setThenStatement(group);
        } else {
            statement.setElseStatement(group);
        }
        delegationCtx.removeAllPreceeding();
        return true;
    }
}
