package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.Operator.OperatorAdd;
import com.github.mhdirkse.countlang.ast.Operator.OperatorDivide;
import com.github.mhdirkse.countlang.ast.Operator.OperatorMultiply;
import com.github.mhdirkse.countlang.ast.Operator.OperatorSubtract;
import com.github.mhdirkse.countlang.ast.Operator.OperatorAnd;
import com.github.mhdirkse.countlang.ast.Operator.OperatorOr;
import com.github.mhdirkse.countlang.ast.Operator.OperatorNot;
import com.github.mhdirkse.countlang.ast.Operator.OperatorEquals;
import com.github.mhdirkse.countlang.ast.Operator.OperatorNotEquals;
import com.github.mhdirkse.countlang.ast.Operator.OperatorLessThan;
import com.github.mhdirkse.countlang.ast.Operator.OperatorLessEqual;
import com.github.mhdirkse.countlang.ast.Operator.OperatorGreaterThan;
import com.github.mhdirkse.countlang.ast.Operator.OperatorGreaterEqual;

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
        } else if (text.equals("and")) {
            expression.setOperator(new OperatorAnd(expression.getLine(), expression.getColumn()));
        } else if (text.equals("or")) {
            expression.setOperator(new OperatorOr(expression.getLine(), expression.getColumn()));
        } else if (text.equals("not")) {
            expression.setOperator(new OperatorNot(expression.getLine(), expression.getColumn()));
        } else if (text.equals("<=")) {
            expression.setOperator(new OperatorLessEqual(expression.getLine(), expression.getColumn()));
        } else if (text.equals("<")) {
            expression.setOperator(new OperatorLessThan(expression.getLine(), expression.getColumn()));
        } else if (text.equals(">=")) {
            expression.setOperator(new OperatorGreaterEqual(expression.getLine(), expression.getColumn()));
        } else if (text.equals(">")) {
            expression.setOperator(new OperatorGreaterThan(expression.getLine(), expression.getColumn()));
        } else if (text.equals("==")) {
            expression.setOperator(new OperatorEquals(expression.getLine(), expression.getColumn()));
        } else if (text.equals("!=")) {
            expression.setOperator(new OperatorNotEquals(expression.getLine(), expression.getColumn()));
        }
    }
}
