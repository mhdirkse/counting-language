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

package com.github.mhdirkse.countlang.type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TupleType extends CountlangType {
    private final List<CountlangType> subTypes;

    TupleType(CountlangType head, CountlangType tail) {
        super(CountlangType.Kind.TUPLE, head, tail);
        this.subTypes = createTupleSubTypes(head, tail);
    }

    private static List<CountlangType> createTupleSubTypes(CountlangType head, CountlangType tail) {
        List<CountlangType> result = new ArrayList<>();
        result.add(head);
        if(tail.isTuple()) {
            result.addAll( ((TupleType) tail).getTupleSubTypes());
        } else {
            result.add(tail);
        }
        return result;
    }

    public List<CountlangType> getTupleSubTypes() {
        return subTypes;
    }

    public int getNumSubTypes() {
    	return subTypes.size();
    }

    @Override
    public String toString() {
        return "tuple<" + subTypes.stream().map(Object::toString).collect(Collectors.joining(", ")) + ">";
    }
}
