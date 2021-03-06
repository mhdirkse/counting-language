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

package com.github.mhdirkse.countlang.ast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class CountlangType {
    private enum Kind {
        UNKNOWN,
        INT,
        BOOL,
        DISTRIBUTION;
    }

    private static final Set<Kind> PRIMITIVES = EnumSet.of(Kind.INT, Kind.BOOL);

    private static final Map<CountlangType, CountlangType> repository = new HashMap<>();

    private final Kind kind;
    private final CountlangType subType;

    private CountlangType(final Kind kind, final CountlangType subType) {
        this.kind = kind;
        this.subType = subType;
    }

    public static CountlangType unknown() {
        return primitive(Kind.UNKNOWN);
    }

    private static CountlangType primitive(Kind kind) {
        CountlangType key = new CountlangType(kind, null);
        if(! repository.containsKey(key)) {
            repository.put(key, key);
        }
        return repository.get(key);
    }

    public static CountlangType integer() {
        return primitive(Kind.INT);
    }

    public static CountlangType bool() {
        return primitive(Kind.BOOL);
    }

    public static CountlangType distributionOf(CountlangType subType) {
        CountlangType key = new CountlangType(Kind.DISTRIBUTION, subType);
        if(! repository.containsKey(key)) {
            repository.put(key, key);
        }
        return repository.get(key);
    }

    public boolean isDistribution() {
        return kind == Kind.DISTRIBUTION;
    }

    public boolean isPrimitive() {
        return PRIMITIVES.contains(kind);
    }

    public CountlangType getSubType() {
        return subType;
    }

    @Override
    public String toString() {
        if(isPrimitive()) {
            return kind.name().toLowerCase();
        } else if(kind.equals(Kind.UNKNOWN)) {
            return "unknown";
        } else {
            return String.format("%s<%s>", kind.name().toLowerCase(), getSubType().toString());
        }
    }
}
