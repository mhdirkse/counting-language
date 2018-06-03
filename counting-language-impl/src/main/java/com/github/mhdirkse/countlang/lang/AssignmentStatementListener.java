package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.Symbol;

class AssignmentStatementListener extends AbstractChildExpressionListener {
    private final AssignmentStatement statement;
    private final AbstractListener parent;

    AssignmentStatementListener(final int line, final int column, AbstractListener parent) {
        this.statement = new AssignmentStatement(line, column); 
        this.parent = parent;
    }

    @Override
    public void handleChildExpression(final Expression expression) {
        statement.setRhs(expression);
    }

    @Override
    public void visitTerminalImpl(final TerminalNode node) {
        Token token = node.getSymbol();
        if (token.getType() == CountlangLexer.ID) {
            statement.setLhs(new Symbol(token.getText()));
        }
    }

    @Override
    public void exitAssignmentStatementImpl(CountlangParser.AssignmentStatementContext ctx) {
        parent.visitAssignmentStatement(statement);
        delegate = null;
    }
}
