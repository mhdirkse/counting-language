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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.format.Format;
import com.github.mhdirkse.countlang.utils.ListComparator;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class CountlangArray implements Comparable<CountlangArray>, CountlangComposite {
    private final List<Object> items;

    public CountlangArray(List<Object> items) {
        this.items = items;
    }

    @Override
    public Object get(int i) {
        return items.get(i);
    }

    @Override
    public List<Object> getAll() {
    	return new ArrayList<>(items);
    }

    @Override
    public int size() {
        return items.size();
    }

    @SuppressWarnings("unchecked")
    public List<Comparable<Object>> getMembers() {
        return items.stream().map(i -> (Comparable<Object>) i).collect(Collectors.toList());
    }

    /**
     * Gives the member indices in the order that will sort the members. Example: Consider
     * members [20, 40, 30, 10]. To sort the members, one needs to take [4, 1, 3, 2]. 
     */
    public List<BigInteger> getSortRefs() {
    	List<IndexAndItem> toSort = new ArrayList<>(items.size());
    	for(int i = 0; i < items.size(); ++i) {
    		toSort.add(new IndexAndItem(i, items.get(i)));
    	}
    	return toSort.stream().sorted().map(s -> s.index).collect(Collectors.toList());
    }

    private static class IndexAndItem implements Comparable<IndexAndItem> {
    	BigInteger index;
    	Comparable<Object> item;

    	@SuppressWarnings("unchecked")
		IndexAndItem(int index, Object o) {
    		this.index = BigInteger.valueOf(index + 1);
    		this.item = (Comparable<Object>) o;
    	}

    	@Override
    	public int compareTo(IndexAndItem o) {
    		return item.compareTo(o.item);
    	}
    }

    @Override
    public int compareTo(CountlangArray o) {
        return ListComparator.getInstance().compare(items, o.items);
    }

    @Override
    public String toString() {
        return Format.EXACT.format(this);
    }
}
