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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.mhdirkse.countlang.ast.DereferenceExpression;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangComposite;
import com.github.mhdirkse.countlang.type.CountlangTuple;

final class DereferenceExpressionCalculation extends ExpressionResultsCollector {
    DereferenceExpressionCalculation(final DereferenceExpression node) {
        super(node);
    }

    @Override
    final void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        DereferenceExpression expr = (DereferenceExpression) getAstNode();
    	CountlangComposite container = (CountlangComposite) subExpressionResults.get(0);
        if(expr.getArraySelector()) {
        	List<Object> newSubValues = IntStream.range(1, subExpressionResults.size()).boxed()
        			.map(i -> getSelectedValue(i, subExpressionResults, container))
        			.collect(Collectors.toList());
        	if(expr.getCountlangType().isArray()) {
        		context.onResult(new CountlangArray(newSubValues));
        	} else if(expr.getCountlangType().isTuple()) {
        		context.onResult(new CountlangTuple(newSubValues));	
        	} else {
        		throw new ProgramException(expr.getLine(), expr.getColumn(), "Programming error detected, expected array or tuple");
        	}
        } else {
        	context.onResult(getSelectedValue(1, subExpressionResults, container));        	
        }
    }

    private Object getSelectedValue(int subExpressionNumber, List<Object> subExpressionResults, CountlangComposite container) {
    	BigInteger index = (BigInteger) subExpressionResults.get(subExpressionNumber);
    	if(index.compareTo(BigInteger.ONE) < 0) {
            throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), String.format("Invalid array index %s", index.toString()));
        }
        if(index.compareTo(BigInteger.valueOf((long) container.size())) > 0) {
            throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), String.format("Array index %s more than array size %d", index.toString(), subExpressionResults.size()));
        }
        int indexToExtract = (int) index.longValue() - 1;
        return container.get(indexToExtract);
    }
}
