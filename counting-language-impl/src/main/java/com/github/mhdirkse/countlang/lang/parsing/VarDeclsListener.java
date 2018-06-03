package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class VarDeclsListener extends AbstractListener {
    final FunctionDefinitionStatement target;
    final VarDeclsListenerCallback parent;

    VarDeclsListener(final FunctionDefinitionStatement target, final VarDeclsListenerCallback parent) {
        this.target = target;
        this.parent = parent;
    }

    @Override
    public void visitTerminalImpl(@NotNull TerminalNode node) {
        if (node.getSymbol().getType() == CountlangParser.ID) {
            target.addFormalParameter(node.getText());
        }
    }

    @Override
    public void exitVarDecls(@NotNull CountlangParser.VarDeclsContext ctx) {
        parent.onVarDeclsDone();
    }
}
