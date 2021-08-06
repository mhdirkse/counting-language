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

package com.github.mhdirkse.countlang.utils;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

public class Utils {
    private Utils() {
    }

    public static String formatLineColumnMessage(
            final int line, final int charPositionInLine, String msg) {
        return String.format("(%d, %d): ", line, charPositionInLine) + msg;
    }

    public static String genericFormat(Object value) {
        if(value instanceof BigFraction) {
            return formatBigFraction((BigFraction) value);
        } else {
            return value.toString();
        }
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
