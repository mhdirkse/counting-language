/*
 * Copyright Martijn Dirkse 2021
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.DereferenceExpression;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.type.AbstractRange;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangComposite;
import com.github.mhdirkse.countlang.type.CountlangTuple;
import com.github.mhdirkse.countlang.type.FractionRange;
import com.github.mhdirkse.countlang.type.IntegerRange;
import com.github.mhdirkse.countlang.type.InvalidRangeException;
import com.github.mhdirkse.countlang.type.RangeIndexOutOfBoundsException;

final class DereferenceExpressionCalculation extends ExpressionResultsCollector {
    DereferenceExpressionCalculation(final DereferenceExpression node) {
        super(node);
    }

    @Override
    final void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        DereferenceExpression expr = (DereferenceExpression) getAstNode();
    	CountlangComposite container = (CountlangComposite) subExpressionResults.get(0);
        if(expr.getArraySelector()) {
        	List<Object> newSubValues = new ArrayList<>();
        	for(int i = 1; i < subExpressionResults.size(); ++i) {
        		if(subExpressionResults.get(i) instanceof AbstractRange) {
        			newSubValues.addAll(getSelectedValuesFromRange((AbstractRange<?>) subExpressionResults.get(i), container));
        		} else {
        			newSubValues.add(getSelectedValue( (BigInteger) subExpressionResults.get(i), container));
        		}
        	}
        	if(expr.getCountlangType().isArray()) {
        		context.onResult(new CountlangArray(newSubValues));
        	} else if(expr.getCountlangType().isTuple()) {
        		context.onResult(new CountlangTuple(newSubValues));	
        	} else {
        		throw new ProgramException(expr.getLine(), expr.getColumn(), "Programming error detected, expected array or tuple");
        	}
        } else {
        	context.onResult(getSelectedValue((BigInteger) subExpressionResults.get(1), container));        	
        }
    }

    private List<Object> getSelectedValuesFromRange(AbstractRange<?> rawRange, CountlangComposite container) {
    	if(rawRange instanceof FractionRange) {
    		throw new IllegalArgumentException("Cannot dereference from fractions, should have been detected during type checking");
    	}
    	IntegerRange range = (IntegerRange) rawRange;
    	try {
    		return range.dereference(container.getAll());
    	} catch(InvalidRangeException e) {
    		throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), e.getMessage());
    	} catch(RangeIndexOutOfBoundsException e) {
    		throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), String.format("Index out of bounds: size = %d, index = %s",
    				container.size(), e.getOffendingIndex().toString()));
    	}
    }

    private Object getSelectedValue(BigInteger index, CountlangComposite container) {
    	if(index.compareTo(BigInteger.ONE) < 0) {
            throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), String.format("Invalid array index %s", index.toString()));
        }
        if(index.compareTo(BigInteger.valueOf((long) container.size())) > 0) {
            throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), String.format("Array index %s more than array size %d", index.toString(), container.size()));
        }
        int indexToExtract = (int) index.longValue() - 1;
        return container.get(indexToExtract);
    }
}
