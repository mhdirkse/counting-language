package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.github.mhdirkse.countlang.utils.Stack;

public class FunctionAndReturnCheckSimple<T, C extends FunctionAndReturnCheck.SimpleContext<T>>
implements FunctionAndReturnCheck<T> {
    final Stack<C> enteredFunctions;
    final Supplier<C> contextFactory;

    FunctionAndReturnCheckSimple(Supplier<C> contextFactory) {
        this.enteredFunctions = new Stack<C>();
        this.contextFactory = contextFactory;
        this.enteredFunctions.push(contextFactory.get());
    }

    @Override
    public void onFunctionEntered() {
        enteredFunctions.push(contextFactory.get());
    }
    
    @Override
    public void onFunctionLeft() {
        enteredFunctions.pop();
    }

    /**
     * 
     * @return The recursion depth of onFunctionEntered
     * and onFunctionLeft calls.
     */
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
    public int getNumReturnValues() {
        return enteredFunctions.peek().returnTypes.size();
    }

    /**
     * 
     * @return The list of return arguments of the innermost function
     * entered with onFunctionEntered.
     */
    public List<T> getReturnValues() {
        List<T> result = new ArrayList<>();
        result.addAll(enteredFunctions.peek().returnTypes);
        return result;
    }

    public void setStop() {
        enteredFunctions.peek().stop = true;
    }

    public boolean isStop() {
        return enteredFunctions.peek().stop;
    }
}
