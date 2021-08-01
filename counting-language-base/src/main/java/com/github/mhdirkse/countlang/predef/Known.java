package com.github.mhdirkse.countlang.predef;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.ast.PredefinedOneArgFunction;

public class Known implements PredefinedOneArgFunction {
    @Override
    public FunctionKey getKey() {
        return new FunctionKey("known", CountlangType.distributionOfAny());
    }

    @Override
    public CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler) {
        if(arguments.size() != 1) {
            errorHandler.handleParameterCountMismatch(1, arguments.size());
            return null;
        }
        CountlangType thisArg = arguments.get(0);
        if(! thisArg.isDistribution()) {
            errorHandler.handleParameterTypeMismatch(0, CountlangType.distributionOfAny(), arguments.get(0));
            return null;
        }
        return arguments.get(0);
    }

    @Override
    public Object run(Object arg) {
        return ((Distribution) arg).getDistributionOfKnown();
    }
}
