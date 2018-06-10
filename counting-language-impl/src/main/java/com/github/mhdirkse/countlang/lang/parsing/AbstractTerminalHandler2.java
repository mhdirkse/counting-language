package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;

abstract class AbstractTerminalHandler2
extends AbstractCountlangListenerHandler
implements TerminalStrategyCallback2
{
    static int ANY_TYPE = -1;

    AbstractTerminalHandler2() {
        super(false);
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode terminalNode,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if (delegationCtx.isFirst() && isRequiredType(terminalNode)) {
            setText(terminalNode.getText());
            return true;
        } else {
            return false;
        }
    }

    private boolean isRequiredType(final TerminalNode terminalNode) {
        int actualType = terminalNode.getSymbol().getType();
        int requiredType = getRequiredType();
        return (requiredType == ANY_TYPE) || (actualType == requiredType);
    }
}
