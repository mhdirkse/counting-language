package com.github.mhdirkse.countlang.lang;

import java.util.ArrayDeque;
import java.util.Deque;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.OperatorAdd;
import com.github.mhdirkse.countlang.ast.OperatorDivide;
import com.github.mhdirkse.countlang.ast.OperatorMultiply;
import com.github.mhdirkse.countlang.ast.OperatorSubtract;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.Symbol;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.Value;
import com.github.mhdirkse.countlang.ast.ValueExpression;

abstract class InStatement extends CountlangBaseListener {
    private Deque<Expression> expressions = new ArrayDeque<Expression>();

    @Override
    public void enterMultDifExpression(final CountlangParser.MultDifExpressionContext ctx) {
        expressions.addLast(new CompositeExpression());
    }

    @Override
    public void exitMultDifExpression(final CountlangParser.MultDifExpressionContext ctx) {
        exitExpression();
    }
 
    private void exitExpression() {
        Expression finished = expressions.removeLast();
        afterExpressionFinished(finished);
    }

    private void afterExpressionFinished(final Expression expression) {
        if (expressions.size() >= 1) {
            addArgument(expressions.getLast(), expression);
        } else {
            handleExpression(expression);
        }
    }

    private void addArgument(final Expression addTo, final Expression argument) {
        if (addTo instanceof CompositeExpression) {
            CompositeExpression cast = (CompositeExpression) addTo;
            cast.addSubExpression(argument);
        } else {
            throw new IllegalStateException("Expected a composite expression to add to");
        }
    }

    @Override
    public void enterPlusMinusExpression(final CountlangParser.PlusMinusExpressionContext ctx) {
        expressions.addLast(new CompositeExpression());
    }

    @Override
    public void exitPlusMinusExpression(final CountlangParser.PlusMinusExpressionContext ctx) {
        exitExpression();
    }
 
    @Override
    public void enterSymbolReferenceExpression(final CountlangParser.SymbolReferenceExpressionContext ctx) {
        expressions.addLast(new SymbolExpression());
    }

    @Override
    public void exitSymbolReferenceExpression(final CountlangParser.SymbolReferenceExpressionContext ctx) {
        exitExpression();
    }

    @Override
    public void enterValueExpression(final CountlangParser.ValueExpressionContext ctx) {
        expressions.addLast(new ValueExpression());
    }

    @Override
    public void exitValueExpression(final CountlangParser.ValueExpressionContext ctx) {
        exitExpression();
    }

    @Override
    public void visitTerminal(final TerminalNode terminalNode) {
        if (expressions.size() >= 1) {
            handleTerminalInsideExpression(terminalNode);
        } else {
            handleTerminalOutsideExpression(terminalNode);
        }
    }

    private void handleTerminalInsideExpression(final TerminalNode terminalNode) {
        Expression top = expressions.getLast();
        if (top instanceof CompositeExpression) {
            visitTerminalForCompositeExpression((CompositeExpression) top, terminalNode);
        } else if (top instanceof SymbolExpression) {
            visitTerminalForSymbolExpression((SymbolExpression) top, terminalNode);
        } else if (top instanceof ValueExpression) {
            visitTerminalForValueExpression((ValueExpression) top, terminalNode);
        } else {
            throw new IllegalStateException("Unknown type of expression on top of stack");
        }
    }

    private void visitTerminalForCompositeExpression(
            final CompositeExpression expression, final TerminalNode terminalNode) {
        String text = terminalNode.getText();
        if (text.equals("*")) {
            expression.setOperator(new OperatorMultiply());
        } else if (text.equals("/")) {
            expression.setOperator(new OperatorDivide());
        } else if (text.equals("+")) {
            expression.setOperator(new OperatorAdd());
        } else if (text.equals("-")) {
            expression.setOperator(new OperatorSubtract());
        }
    }

    private void visitTerminalForSymbolExpression(
            final SymbolExpression expression, final TerminalNode terminalNode) {
        if (terminalNode.getSymbol().getType() == CountlangParser.ID) {
            expression.setSymbol(new Symbol(terminalNode.getText()));
        }
    }

    private void visitTerminalForValueExpression(
            final ValueExpression expression, final TerminalNode terminalNode) {
        if (terminalNode.getSymbol().getType() == CountlangParser.INT) {
            expression.setValue(new Value(Integer.valueOf(terminalNode.getText())));
        }
    }

    /**
     * Override this method to handle terminal nodes that are not
     * part of the expression.
     *
     * @param terminalNode The terminal node to handle.
     */
    void handleTerminalOutsideExpression(final TerminalNode terminalNode) {
    }

    abstract Statement getStatement();
    abstract void handleExpression(final Expression expression);
}
