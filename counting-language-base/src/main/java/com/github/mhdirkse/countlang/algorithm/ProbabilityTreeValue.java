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

package com.github.mhdirkse.countlang.algorithm;

import lombok.Getter;

public class ProbabilityTreeValue implements Comparable<ProbabilityTreeValue> {
    private final @Getter boolean unknown;
    private final Object value;

    private ProbabilityTreeValue(Object value) {
        this.unknown = false;
        this.value = value;
    }

    private ProbabilityTreeValue() {
        this.unknown = true;
        value = null;
    }

    private static final ProbabilityTreeValue UNKNOWN = new ProbabilityTreeValue();

    static ProbabilityTreeValue unknown() {
        return UNKNOWN;
    }

    static ProbabilityTreeValue of(Object value) {
        if(value == null) {
            throw new IllegalArgumentException("A ProbabilityTreeValue cannot wrap null");
        }
        return new ProbabilityTreeValue(value);
    }

    public Object getValue() {
        if(unknown) {
            throw new IllegalStateException("The unknown ProbabilityTreeValue has no normal value");
        }
        return value;
    }

    @Override
    public int compareTo(ProbabilityTreeValue other) {
        if(this.isUnknown()) {
            if(other.isUnknown()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if(other.isUnknown()) {
                return -1;
            } else {
                return compareValue(other);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private int compareValue(ProbabilityTreeValue other) {
        return ((Comparable<Object>) this.getValue()).compareTo(other.getValue());
    }

    @Override
    public String toString() {
        if(isUnknown()) {
            return "unknown";
        } else {
            return getValue().toString();
        }
    }
}
