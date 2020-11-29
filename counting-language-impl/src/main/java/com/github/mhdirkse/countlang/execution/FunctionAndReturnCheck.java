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

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;

public interface FunctionAndReturnCheck<T> extends BranchHandler {
    void onFunctionEntered(String name, boolean isExperiment);
    void onFunctionLeft();
    BranchingReturnCheck.Status getReturnStatus();
    public int getNestedFunctionDepth();
    void onReturn(int line, int column, List<T> values);
    int getNumReturnValues();
    List<T> getReturnValues();
    void setStop();
    boolean isStop();
    boolean isInExperiment();

    static class SimpleContext<T> {
        List<T> returnTypes;
        final BranchingReturnCheckImpl returnCheck = new BranchingReturnCheckImpl();
        boolean stop = false;
        final String functionName;
        final boolean isExperiment;

        public SimpleContext(final String functionName, Boolean isExperiment) {
            returnTypes = new ArrayList<>();
            this.functionName = functionName;
            this.isExperiment = isExperiment.booleanValue();
        }
    }

    static class TypeCheckContext extends SimpleContext<CountlangType> {
        boolean hasReturn = false;
        int lineFirstReturn = 0;
        int columnFirstReturn = 0;
        
        public TypeCheckContext(final String name, Boolean isExperiment) {
            super(name, isExperiment);
        }
    }
}
