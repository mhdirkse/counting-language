package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;

public interface FunctionAndReturnCheck<T> {
    void onFunctionEntered();
    void onFunctionLeft();
    void onReturn(int line, int column, List<T> values);

    static class SimpleContext<T> {
        List<T> returnTypes;
        final BranchingReturnCheckImpl returnCheck = new BranchingReturnCheckImpl();
        boolean stop = false;

        SimpleContext() {
            returnTypes = new ArrayList<>();
        }
    }

    static class TypeCheckContext extends SimpleContext<CountlangType> {
        boolean hasReturn = false;
        int lineFirstReturn = 0;
        int columnFirstReturn = 0;
        
        TypeCheckContext() {
            super();
        }
    }
}
