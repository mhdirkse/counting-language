package com.github.mhdirkse.countlang.lang.parsing;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.AbstractTupleDealingLhsItem;
import com.github.mhdirkse.countlang.ast.TupleDealingLhsItemSkipped;
import com.github.mhdirkse.countlang.ast.TupleDealingLhsSymbol;
import com.github.mhdirkse.countlang.lang.CountlangLexer;
import com.github.mhdirkse.countlang.lang.CountlangParser;

import lombok.Getter;

class LeftHandSideStrategy extends AbstractCountlangListenerHandler {
    @Getter
    private List<AbstractTupleDealingLhsItem> lhsItems = new ArrayList<>();

    private int line;
    private int column;
    private int variableNumber = 0;

    @Getter
    private boolean inLhsItem = false;

    LeftHandSideStrategy() {
        super(false);
    }

    @Override
    public boolean enterLhsItem(CountlangParser.LhsItemContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        inLhsItem = true;
        line = antlrCtx.start.getLine();
        column = antlrCtx.start.getCharPositionInLine();
        return true;
    }

    @Override
    public boolean exitLhsItem(CountlangParser.LhsItemContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        inLhsItem = false;
        line = 0;
        column = 0;
        return true;
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode node, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(! inLhsItem) {
            throw new IllegalStateException("Expected to be inside an lhsItem rule");
        }
        if(node.getSymbol().getTokenIndex() == CountlangLexer.LHS_PLACEHOLDER) {
            TupleDealingLhsItemSkipped item = new TupleDealingLhsItemSkipped(line, column);
            item.setVariableNumber(variableNumber);
            lhsItems.add(item);
        } else if(node.getSymbol().getTokenIndex() == CountlangLexer.ID) {
            TupleDealingLhsSymbol item = new TupleDealingLhsSymbol(line, column);
            item.setVariableNumber(variableNumber);
            item.setSymbol(node.getText());
            lhsItems.add(item);
        } else {
            throw new IllegalArgumentException(String.format("Unexpected token with text %s", node.getSymbol().getText()));
        }
        ++variableNumber;
        return true;
    }
}
