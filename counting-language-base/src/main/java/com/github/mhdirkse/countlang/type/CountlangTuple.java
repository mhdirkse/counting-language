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

package com.github.mhdirkse.countlang.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mhdirkse.countlang.format.Format;
import com.github.mhdirkse.countlang.utils.ListComparator;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class CountlangTuple implements Comparable<CountlangTuple>, CountlangComposite {
    private final List<Object> members;

    public CountlangTuple(List<Object> members) {
        this.members = flattenMembers(members);
    }

    private static List<Object> flattenMembers(List<Object> orig) {
        List<Object> result = new ArrayList<>();
        for(Object member: orig) {
            if(member instanceof CountlangTuple) {
                result.addAll(((CountlangTuple) member).getMembers());
            } else {
                result.add(member);
            }
        }
        return result;
    }

    public List<Object> getMembers() {
        return Collections.unmodifiableList(members);
    }

    @Override
    public List<Object> getAll() {
    	return new ArrayList<>(members);
    }

    @Override
    public int size() {
        return members.size();
    }

    @Override
    public Object get(int i) {
        return members.get(i);
    }

    @Override
    public int compareTo(CountlangTuple o) {
        return ListComparator.getInstance().compare(this.members, o.members);
    }

    @Override
    public String toString() {
        return Format.EXACT.format(this);
    }
}
