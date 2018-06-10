package com.github.mhdirkse.countlang.lang.parsing;

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class IgnoredMethodsHandler extends AbstractCountlangListenerHandler {
    private final Set<Integer> RELEVANT_TOKENS = new HashSet<>(
            CountlangParser.ID, CountlangParser.INT);

    IgnoredMethodsHandler() {
        super(false);
    }

    @Override
    public boolean enterEveryRule(
            final ParserRuleContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean exitEveryRule(
            final ParserRuleContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean enterStatements (
            final CountlangParser.StatementsContext p1,
            final HandlerStackContext<CountlangListenerHandler> ctx) {
        return true;
    }

    @Override
    public boolean exitStatements (
            final CountlangParser.StatementsContext p1,
            final HandlerStackContext<CountlangListenerHandler> ctx) {
        return true;
    }

    @Override
    public boolean enterBracketExpression(CountlangParser.BracketExpressionContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean exitBracketExpression(
            final @NotNull CountlangParser.BracketExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean visitTerminal(
            final @NotNull TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if (RELEVANT_TOKENS.contains(node.getSymbol().getType())) {
            return false;
        } else {
            return true;
        }
    }
}
