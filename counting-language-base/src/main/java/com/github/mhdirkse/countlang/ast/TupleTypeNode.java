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

package com.github.mhdirkse.countlang.ast;

import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.type.CountlangType;

public class TupleTypeNode extends CompositeTypeNode {
    public TupleTypeNode(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getCountlangType() {
        List<CountlangType> childTypes = children.stream().map(TypeNode::getCountlangType).collect(Collectors.toList());
        if(childTypes.size() >= 2) {
            return CountlangType.tupleOf(childTypes);            
        } else {
            return CountlangType.unknown();
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitTupleTypeNode(this);
    }
}
