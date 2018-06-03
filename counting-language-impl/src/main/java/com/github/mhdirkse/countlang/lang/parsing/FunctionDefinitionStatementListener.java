package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class FunctionDefinitionStatementListener extends AbstractStatementListener implements VarDeclsListenerCallback {
    private final FunctionDefinitionStatement statement;
    private final AstNode.Visitor parent;

    FunctionDefinitionStatementListener(final int line, final int column, final AstNode.Visitor parent) {
        statement = new FunctionDefinitionStatement(line, column);
        this.parent = parent;
    }

    @Override
    public void visitTerminalImpl(@NotNull TerminalNode node) {
        if (node.getSymbol().getType() == CountlangParser.ID) {
            statement.setName(node.getText());
        }
    }

    @Override
    public void enterVarDecls(@NotNull CountlangParser.VarDeclsContext ctx) {
        delegate = new VarDeclsListener(statement, this);
    }

    @Override
    void handleStatement(final Statement childStatement) {
        statement.addStatement(childStatement);
        delegate = null;
    }

    @Override
    public void onVarDeclsDone() {
        delegate = null;
    }

    @Override
    public void exitFunctionDefinitionStatementImpl(@NotNull CountlangParser.FunctionDefinitionStatementContext ctx) {
        parent.visitFunctionDefinitionStatement(statement);
        delegate = null;
    }
}
