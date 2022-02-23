package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.Call;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class CallHandler extends AbstractExpressionHandler implements TerminalFilterCallback {
    private final TerminalFilter terminalFilter;
    Call call;

    CallHandler(Call target) {
    	call = target;
        terminalFilter = new TerminalFilter(this);
    }

    @Override
    public void addExpression(final ExpressionNode childExpression) {
        call.addArgument(childExpression);
    }

    @Override
    public boolean visitTerminal(
            @NotNull final TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalFilter.visitTerminal(node, delegationCtx);
    }

    @Override
    public int getRequiredType() {
        return CountlangParser.ID;
    }

    @Override
    public void setText(final String text) {
        call.setName(text);
    }
}
