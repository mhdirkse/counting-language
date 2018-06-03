package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.OperatorAdd;
import com.github.mhdirkse.countlang.ast.OperatorDivide;
import com.github.mhdirkse.countlang.ast.OperatorMultiply;
import com.github.mhdirkse.countlang.ast.OperatorSubtract;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class OperatorExpressionListener extends AbstractChildExpressionListener {
    final CompositeExpression result;
    final AstNode.Visitor parent;

    OperatorExpressionListener(final int line, final int column, final AstNode.Visitor parent) {
        result = new CompositeExpression(line, column);
        this.parent = parent;
    }

    @Override
    void handleChildExpression(final Expression expression) {
        result.addSubExpression(expression);
        delegate = null;
    }

    @Override
    void visitTerminalImpl(final TerminalNode node) {
        String text = node.getText();
        if (text.equals("*")) {
            result.setOperator(new OperatorMultiply());
        } else if (text.equals("/")) {
            result.setOperator(new OperatorDivide());
        } else if (text.equals("+")) {
            result.setOperator(new OperatorAdd());
        } else if (text.equals("-")) {
            result.setOperator(new OperatorSubtract());
        }
    }

    void exitMultDifExpressionImpl(CountlangParser.MultDifExpressionContext ctx) {
        close();
    }

    private void close() {
        parent.visitCompositeExpression(result);
        delegate = null;
    }

    void exitPlusMinusExpressionImpl(CountlangParser.PlusMinusExpressionContext ctx) {
        close();
    }
}
