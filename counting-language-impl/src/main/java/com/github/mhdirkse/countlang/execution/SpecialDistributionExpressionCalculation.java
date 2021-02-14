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

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.AbstractDistributionExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.ProgramException;

abstract class SpecialDistributionExpressionCalculation extends SimpleDistributionExpressionCalculation {
    Integer finalValue;

    SpecialDistributionExpressionCalculation(final AbstractDistributionExpression expression) {
        super(expression);
    }

    @Override
    public boolean isAcceptingChildResults() {
        return true;
    }

    @Override
    public void acceptChildResult(Object value, ExecutionContext context) {
        finalValue = (Integer) value;
    }

    @Override
    public void finishBuilder() {
        Distribution.Builder builder = ((Distribution.Builder) getContext());
        int totalScored = builder.getTotal();
        builder.addUnknown(getUnknown(finalValue, totalScored));        
    }

    abstract int getUnknown(int extraSubExpressionResult, int totalScored);

    static final class WithTotal extends SpecialDistributionExpressionCalculation {
        WithTotal(DistributionExpressionWithTotal expression) {
            super(expression);
        }

        @Override
        int getUnknown(int total, int totalScored) {
            if(totalScored > total) {
                throw new ProgramException(
                        getAstNode().getLine(),
                        getAstNode().getColumn(),
                        String.format(
                                "The scored items in the distribution make count %d, which is more than %d",
                                totalScored, total));
            }
            return total - totalScored;
        }
    }

    static final class WithUnknown extends SpecialDistributionExpressionCalculation {
        WithUnknown(DistributionExpressionWithUnknown expression) {
            super(expression);
        }

        @Override
        int getUnknown(int unknown, int totalScored) {
            if(unknown < 0) {
                throw new ProgramException(
                        getAstNode().getLine(),
                        getAstNode().getColumn(),
                        String.format(
                                "The unknown count in a distribution cannot be negative; you tried %d",
                                unknown));
            }
            return unknown;
        }
    }
}
