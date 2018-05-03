package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.Symbol;

final class InAssignmentStatement extends InStatement {
    final private AssignmentStatement assignmentStatement;

    InAssignmentStatement(final int line, final int column) {
        assignmentStatement = new AssignmentStatement(line, column);
    }

    @Override
    final void handleTerminalOutsideExpression(TerminalNode terminalNode) {
        Token token = terminalNode.getSymbol();
        if (token.getType() == CountlangLexer.ID) {
            assignmentStatement.setLhs(new Symbol(token.getText()));
        }
    }

    @Override
    final void handleExpression(final Expression expression) {
        assignmentStatement.setRhs(expression);
    }

    @Override
    final Statement getStatement() {
        return assignmentStatement;
    }
}
