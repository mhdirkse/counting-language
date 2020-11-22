package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.github.mhdirkse.countlang.execution.BranchingReturnCheck.Status;
import com.github.mhdirkse.countlang.utils.Stack;

public class FunctionAndReturnCheckSimple<T, C extends FunctionAndReturnCheck.SimpleContext<T>>
implements FunctionAndReturnCheck<T> {
    final Stack<C> enteredFunctions;
    final BiFunction<String, Boolean, C> contextFactory;

    public FunctionAndReturnCheckSimple(BiFunction<String, Boolean, C> contextFactory) {
        this.enteredFunctions = new Stack<C>();
        this.contextFactory = contextFactory;
        this.enteredFunctions.push(contextFactory.apply("", false));
    }

    @Override
    public void onSwitchOpened() {
        enteredFunctions.peek().returnCheck.onSwitchOpened();
    }

    @Override
    public void onSwitchClosed() {
        enteredFunctions.peek().returnCheck.onSwitchClosed();
    }

    @Override
    public void onBranchOpened() {
        enteredFunctions.peek().returnCheck.onBranchOpened();
    }

    @Override
    public void onBranchClosed() {
        enteredFunctions.peek().returnCheck.onBranchClosed();
    }

    @Override
    public void onFunctionEntered(final String name, boolean isExperiment) {
        enteredFunctions.push(contextFactory.apply(name, isExperiment));
    }
    
    @Override
    public void onFunctionLeft() {
        enteredFunctions.pop();
    }

    @Override
    public Status getReturnStatus() {
        return enteredFunctions.peek().returnCheck.getStatus();
    }

    /**
     * 
     * @return The recursion depth of onFunctionEntered
     * and onFunctionLeft calls.
     */
    @Override
    public int getNestedFunctionDepth() {
        return enteredFunctions.size() - 1;
    }

    @Override
    public void onReturn(int line, int column, List<T> values) {
        enteredFunctions.peek().returnCheck.onReturn();
        enteredFunctions.peek().returnTypes = new ArrayList<>(values); 
    }

    /**
     * 
     * @return The number of return values of the innermost function
     * entered with onFunctionEntered.
     */
    @Override
    public int getNumReturnValues() {
        return enteredFunctions.peek().returnTypes.size();
    }

    /**
     * 
     * @return The list of return arguments of the innermost function
     * entered with onFunctionEntered.
     */
    @Override
    public List<T> getReturnValues() {
        List<T> result = new ArrayList<>();
        result.addAll(enteredFunctions.peek().returnTypes);
        return result;
    }

    @Override
    public void setStop() {
        enteredFunctions.peek().stop = true;
    }

    @Override
    public boolean isStop() {
        return enteredFunctions.peek().stop;
    }

    @Override
    public boolean isInExperiment() {
        return enteredFunctions.peek().isExperiment;
    }
}
