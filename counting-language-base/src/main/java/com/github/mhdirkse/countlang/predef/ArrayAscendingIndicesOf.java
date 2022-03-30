/*
 * Copyright Martijn Dirkse 2022
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
import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangArray;
import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayAscendingIndicesOf extends AbstractMemberFunction {
	public ArrayAscendingIndicesOf() {
		super("ascendingIndicesOf", CountlangType.arrayOfAny(), t -> CountlangType.arrayOf(CountlangType.integer()), t -> t.getSubType());
	}

	@Override
	public Object run(int line, int column, List<Object> args) {
		CountlangArray thisArg = (CountlangArray) args.get(0);
		Object toFind = args.get(1);
		List<Object> members = thisArg.getAll();
		List<BigInteger> result = new ArrayList<>();
		for(int i = 0; i < members.size(); ++i) {
			Object actual = members.get(i);
			if(actual.equals(toFind)) {
				long longIndex = i + 1;
				result.add(BigInteger.valueOf(longIndex));
			}
		}
		List<Object> objectList = new ArrayList<>();
		objectList.addAll(result);
		return new CountlangArray(objectList);
	}
}
