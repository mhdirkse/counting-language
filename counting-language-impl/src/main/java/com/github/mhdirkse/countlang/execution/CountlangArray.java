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

import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.utils.ListComparator;
import com.github.mhdirkse.countlang.utils.Utils;

class CountlangArray implements Comparable<CountlangArray>, CountlangComposite {
    private final List<Object> items;

    CountlangArray(List<Object> items) {
        this.items = items;
    }

    @Override
    public Object get(int i) {
        return items.get(i);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public int compareTo(CountlangArray o) {
        return ListComparator.getInstance().compare(items, o.items);
    }

    @Override
    public String toString() {
        return "[" + items.stream().map(v -> Utils.genericFormat(v)).collect(Collectors.joining(", ")) + "]";
    }
}
