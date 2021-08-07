package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.TypeNode;
import com.github.mhdirkse.countlang.lang.CountlangParser;

public abstract class AbstractTypeIdStrategy extends AbstractCountlangListenerHandler implements TypeIdSource {
    AbstractTypeIdStrategy() {
        super(false);
    }

    @Override
    public boolean enterSimpleType(
            CountlangParser.SimpleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new SimpleTypeHandler(line, column));
        return true;
    }

    @Override
    public boolean exitSimpleType(
            CountlangParser.SimpleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx);
    }

    private boolean handleTypeExit(final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(delegationCtx.isFirst()) {
            return false;
        } else {
            TypeIdSource typeIdSource = ((TypeIdSource) delegationCtx.getPreviousHandler());
            TypeNode child = typeIdSource.getTypeNode();
            handleChild(child);
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }

    abstract void handleChild(TypeNode child);

    @Override
    public boolean enterDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new DistributionTypeIdHandler(line, column));
        return true;
    }

    @Override
    public boolean exitDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx);
    }

    @Override
    public boolean enterArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new ArrayTypeIdHandler(line, column));
        return true;
    }

    @Override
    public boolean exitArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx);
    }

    @Override
    public boolean enterTupleType(
            CountlangParser.TupleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new TupleTypeIdHandler(line, column));
        return true;        
    }

    @Override
    public boolean exitTupleType(
            CountlangParser.TupleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx);
    }    
}
