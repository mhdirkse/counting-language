/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.execution;

import static com.github.mhdirkse.countlang.execution.BranchingReturnCheck.Status.ALL_RETURN;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck.TypeCheckContext;

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
public class FunctionAndReturnTypeCheck
extends FunctionAndReturnCheckSimple<CountlangType, TypeCheckContext>
implements BranchHandler {
    public static interface Callback {
        void reportStatementHasNoEffect(int line, int column, String functionName);
        void reportInconsistentReturnType(int lineOrigType, int columnOrigType, int line, int column, String functionName);
    }
    
    private Callback callback;
    
    public FunctionAndReturnTypeCheck(
            BiFunction<String, Boolean, TypeCheckContext> contextFactory) {
        super(contextFactory);
    }
    
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void onStatement(int line, int column) {
        if(enteredFunctions.peek().returnCheck.getStatus() == ALL_RETURN) {
            callback.reportStatementHasNoEffect(line, column, enteredFunctions.peek().functionName);
        }
    }

    public void onReturn(int line, int column, List<CountlangType> returnTypes) {
        TypeCheckContext context = this.enteredFunctions.peek();
        List<CountlangType> oldReturnTypes = new ArrayList<>(context.returnTypes);
        super.onReturn(line, column, returnTypes);
        if(context.hasReturn) {
            if(!equalReturnTypes(oldReturnTypes, context.returnTypes)) {
                callback.reportInconsistentReturnType(
                        context.lineFirstReturn,
                        context.columnFirstReturn,
                        line, column,
                        context.functionName);
            }
        } else {
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
    
    public boolean hasExplicitReturn() {
        return enteredFunctions.peek().hasReturn;
    }

    public int getLineFirstReturn() {
        return enteredFunctions.peek().lineFirstReturn;
    }

    public int getColumnFirstReturn() {
        return enteredFunctions.peek().columnFirstReturn;
    }
}
