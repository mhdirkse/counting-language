package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.Operator.OperatorAdd;
import com.github.mhdirkse.countlang.ast.Operator.OperatorDivide;
import com.github.mhdirkse.countlang.ast.Operator.OperatorMultiply;
import com.github.mhdirkse.countlang.ast.Operator.OperatorSubtract;

class GeneralOperatorExpressionHandler extends AbstractExpressionHandler
implements ExpressionSource, TerminalFilterCallback {
    private final TerminalFilter terminalFilter;
    private CompositeExpression expression;

    @Override
    public ExpressionNode getExpression() {
        return expression;
    }

    GeneralOperatorExpressionHandler(final int line, final int column) {
        expression = new CompositeExpression(line, column);
        terminalFilter = new TerminalFilter(this);
    }

    @Override
    void addExpression(final ExpressionNode childExpression) {
        expression.addSubExpression(childExpression);
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalFilter.visitTerminal(node, delegationCtx);
    }

    @Override
    public int getRequiredType() {
        return AbstractTerminalHandler.ANY_TYPE;
    }

    @Override
    public void setText(final String text) {
        if (text.equals("*")) {
            expression.setOperator(new OperatorMultiply(expression.getLine(), expression.getColumn()));
        } else if (text.equals("/")) {
            expression.setOperator(new OperatorDivide(expression.getLine(), expression.getColumn()));
        } else if (text.equals("+")) {
            expression.setOperator(new OperatorAdd(expression.getLine(), expression.getColumn()));
        } else if (text.equals("-")) {
            expression.setOperator(new OperatorSubtract(expression.getLine(), expression.getColumn()));
        }
    }
}
