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

package com.github.mhdirkse.countlang.utils;

import java.util.Comparator;
import java.util.List;

public final class ListComparator implements Comparator<List<Object>> {
    private static ListComparator instance = null;

    public static ListComparator getInstance() {
        if(instance == null) {
            instance = new ListComparator();
        }
        return instance;
    }

    private ListComparator() {
    }

    @Override
    public int compare(List<Object> arg0, List<Object> arg1) {
        int numComparable = Math.min(arg0.size(), arg1.size());
        for(int i = 0; i < numComparable; ++i) {
            @SuppressWarnings("unchecked")
            Comparable<Object> first = (Comparable<Object>) arg0.get(i);
            @SuppressWarnings("unchecked")
            Comparable<Object> second = (Comparable<Object>) arg1.get(i);
            int result = first.compareTo(second);
            if(result != 0) {
                return result;
            }
        }
        return Integer.valueOf(arg0.size()).compareTo(arg1.size());
    }
}
