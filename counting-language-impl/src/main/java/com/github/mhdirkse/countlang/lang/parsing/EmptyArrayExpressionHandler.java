package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ArrayExpression;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.lang.CountlangParser;
import com.github.mhdirkse.countlang.type.CountlangType;

public class EmptyArrayExpressionHandler extends AbstractCountlangListenerHandler implements ExpressionSource {
    private ArrayExpression expression;
    private TypeIdHandler typeHandler;

    public EmptyArrayExpressionHandler(int line, int column) {
        super(false);
        typeHandler = new TypeIdHandler(line, column);
        expression = new ArrayExpression(line, column);
    }

    @Override
    public boolean enterSimpleType(
            CountlangParser.SimpleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.enterSimpleType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.enterDistributionType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitSimpleType(
            CountlangParser.SimpleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.exitSimpleType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.exitDistributionType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.enterArrayType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeHandler.exitArrayType(antlrCtx, delegationCtx);
    }

    @Override
    public ExpressionNode getExpression() {
        expression.setCountlangType(CountlangType.arrayOf(typeHandler.getCountlangType()));
        return expression;
    }
}
