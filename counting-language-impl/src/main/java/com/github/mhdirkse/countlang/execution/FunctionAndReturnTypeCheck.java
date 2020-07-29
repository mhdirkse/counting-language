package com.github.mhdirkse.countlang.execution;

import static com.github.mhdirkse.countlang.execution.BranchingReturnCheck.Status.ALL_RETURN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.utils.Stack;

/**
 * Captures branching events and return values to check the following:
 * <ul>
 * <li> Outside a function, there is no return statement.
 * <li> Inside a function, all branches return.
 * <li> After a return, no extra statement appears.
 * <li> Every branch returns a value of the same type.
 * </ul>
 * 
 * This class anticipates on the development of counting-language.
 * It supports nested function definitions and multiple return
 * values. It also supports functions that do not return a value.
 * And functions with a return statement without return values
 * are also supported.
 *
 * @author martijn
 *
 */
public class FunctionAndReturnTypeCheck implements BranchHandler {
    public static interface Callback {
        void reportStatementHasNoEffect(int line, int column);
        void reportInconsistentReturnType(int lineOrigType, int columnOrigType, int line, int column);
    }

    private static class Context {
        List<CountlangType> returnTypes;
        final BranchingReturnCheckImpl returnCheck = new BranchingReturnCheckImpl();
        boolean hasReturn = false;
        int lineFirstReturn = 0;
        int columnFirstReturn = 0;
        boolean stop = false;
        
        Context() {
            returnTypes = new ArrayList<>();
        }
    }
    
    private final Callback callback;
    
    private Stack<Context> enteredFunctions;
    
    public FunctionAndReturnTypeCheck(final Callback callback) {
        this.callback = callback;
        this.enteredFunctions = new Stack<Context>();
        this.enteredFunctions.push(new Context());
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

    public void onStatement(int line, int column) {
        if(enteredFunctions.peek().returnCheck.getStatus() == ALL_RETURN) {
            callback.reportStatementHasNoEffect(line, column);
        }
    }

    public void onReturn(int line, int column, CountlangType ...returnTypes) {
        List<CountlangType> newReturnTypes = new ArrayList<>();
        newReturnTypes.addAll(Arrays.asList(returnTypes));
        onStatement(line, column);
        Context context = this.enteredFunctions.peek();
        context.returnCheck.onReturn();
        if(context.hasReturn) {
            if(!equalReturnTypes(context.returnTypes, newReturnTypes)) {
                callback.reportInconsistentReturnType(
                        context.lineFirstReturn,
                        context.columnFirstReturn,
                        line, column);
            }
        } else {
            context.returnTypes = newReturnTypes; 
            context.hasReturn = true;
            context.lineFirstReturn = line;
            context.columnFirstReturn = column;            
        }
    }

    private static boolean equalReturnTypes(
            final List<CountlangType> first, final List<CountlangType> second) {
        if(first.size() != second.size()) {
            return false;
        }
        for(int i = 0; i < first.size(); i++) {
            if(!first.get(i).equals(second.get(i))) {
                return false;
            }
        }
        return true;
    }

    public void onFunctionEntered() {
        enteredFunctions.push(new Context());
    }
    
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
    public List<CountlangType> getReturnValues() {
        List<CountlangType> result = new ArrayList<>();
        result.addAll(enteredFunctions.peek().returnTypes);
        return result;
    }

    public boolean hasExplicitReturn() {
        return enteredFunctions.peek().hasReturn;
    }

    public int getLineFirstReturn() {
        return enteredFunctions.peek().lineFirstReturn;
    }

    public int getColumnFirstReturn() {
        return enteredFunctions.peek().columnFirstReturn;
    }

    public void setStop() {
        enteredFunctions.peek().stop = true;
    }

    public boolean isStop() {
        return enteredFunctions.peek().stop;
    }
}
