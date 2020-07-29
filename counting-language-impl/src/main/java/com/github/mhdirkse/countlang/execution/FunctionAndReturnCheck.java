package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;

public interface FunctionAndReturnCheck<T> extends BranchHandler {
    void onFunctionEntered(String name);
    void onFunctionLeft();
    BranchingReturnCheck.Status getReturnStatus();
    public int getNestedFunctionDepth();
    void onReturn(int line, int column, List<T> values);
    int getNumReturnValues();
    List<T> getReturnValues();
    void setStop();
    boolean isStop();

    static class SimpleContext<T> {
        List<T> returnTypes;
        final BranchingReturnCheckImpl returnCheck = new BranchingReturnCheckImpl();
        boolean stop = false;
        final String functionName;

        public SimpleContext(final String functionName) {
            returnTypes = new ArrayList<>();
            this.functionName = functionName;
        }
    }

    static class TypeCheckContext extends SimpleContext<CountlangType> {
        boolean hasReturn = false;
        int lineFirstReturn = 0;
        int columnFirstReturn = 0;
        
        public TypeCheckContext(final String name) {
            super(name);
        }
    }
}
