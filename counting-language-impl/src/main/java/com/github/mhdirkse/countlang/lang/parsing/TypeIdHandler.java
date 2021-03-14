package com.github.mhdirkse.countlang.lang.parsing;

import java.util.function.Consumer;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.lang.CountlangParser;

public class TypeIdHandler extends AbstractTypeHandler implements ExpressionSource {
    private CountlangType countlangType = CountlangType.unknown();
    
    TypeIdHandler(int line, int column) {
        super(line, column);
    }

    @Override
    CountlangType getCountlangType() {
        return countlangType;
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
        return handleTypeExit(delegationCtx, subType -> countlangType = subType);
    }

    private boolean handleTypeExit(final HandlerStackContext<CountlangListenerHandler> delegationCtx, Consumer<CountlangType> subTypeHandler) {
        if(delegationCtx.isFirst()) {
            return false;
        } else {
            AbstractTypeHandler typeHandler = ((AbstractTypeHandler) delegationCtx.getPreviousHandler());;
            subTypeHandler.accept(typeHandler.getCountlangType());
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }

    @Override
    public boolean enterDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new TypeIdHandler(line, column));
        return true;
    }

    @Override
    public boolean exitDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleTypeExit(delegationCtx, subType -> countlangType = CountlangType.distributionOf(subType));
    }
}
