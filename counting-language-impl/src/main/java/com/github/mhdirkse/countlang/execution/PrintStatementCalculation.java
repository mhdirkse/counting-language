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

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.PrintStatement;

final class PrintStatementCalculation extends ExpressionResultsCollector {
    PrintStatementCalculation(PrintStatement node) {
        super(node);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        Object value = subExpressionResults.get(0);
        String output = value.toString();
        if(value instanceof BigFraction) {
            output = formatBigFraction((BigFraction) value);
        }
        if(value instanceof Distribution) {
            output = ((Distribution) value).format();
        }
        context.output(output);
    }

    private static String formatBigFraction(BigFraction b) {
        if(b.compareTo(BigFraction.ZERO) == 0) {
            return "0";
        }
        BigFraction v = b;
        String firstSign = "";
        String secondSign = "+";
        if(b.compareTo(BigFraction.ZERO) < 0) {
            v = b.negate();
            firstSign = "-";
            secondSign = "-";
        }
        BigInteger[] divAndRem = v.getNumerator().divideAndRemainder(v.getDenominator());
        BigInteger div = divAndRem[0];
        BigInteger rem = divAndRem[1];
        if(div.compareTo(BigInteger.ZERO) == 0) {
            return String.format("%s%s / %s", firstSign, rem.toString(), v.getDenominator().toString());
        } else if(rem.compareTo(BigInteger.ZERO) == 0) {
            return String.format("%s%s", firstSign, div.toString());
        } else {
            return String.format("%s%s %s %s / %s", firstSign, div.toString(), secondSign, rem.toString(), v.getDenominator().toString());
        }
    }
}
