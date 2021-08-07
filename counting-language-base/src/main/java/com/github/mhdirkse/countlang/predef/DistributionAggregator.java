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

package com.github.mhdirkse.countlang.predef;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.type.CountlangType;

public abstract class DistributionAggregator extends DistributionMemberNoArguments {
    DistributionAggregator(String name, CountlangType ownerType, CountlangType returnType) {
        super(name, ownerType, returnType);
    }

    @Override
    public Object run(int line, int column, List<Object> args) {
        Distribution d = (Distribution) args.get(0);
        if(d.getCountUnknown().compareTo(BigInteger.ZERO) != 0) {
            throw new ProgramException(line, column, "Cannot execute sum() on distribution that has unknown");
        }
        Object result = getInitialResult();
        Iterator<Object> it = d.getItemIterator();
        while(it.hasNext()) {
            Object item = it.next();
            BigInteger count = d.getCountOf(item);
            result = applyNext(result, item, count);
        }
        return finish(line, column, d, result);
    }

    abstract Object getInitialResult();
    abstract Object applyNext(Object original, Object item, BigInteger count);
    abstract Object finish(int line, int column, Distribution input, Object originalAggregate);
}
