package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;

abstract class AbstractTerminalHandler
extends AbstractCountlangListenerHandler
implements TerminalFilterCallback
{
    static int ANY_TYPE = -1;

    AbstractTerminalHandler() {
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
