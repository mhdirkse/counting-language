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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class CountlangType {
    private enum Kind {
        UNKNOWN,
        ANY,
        FRACTION,
        INT,
        BOOL,
        DISTRIBUTION,
        ARRAY;
    }

    private static final Set<Kind> PRIMITIVES = EnumSet.of(Kind.FRACTION, Kind.INT, Kind.BOOL);

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

    public static CountlangType fraction() {
    	return primitive(Kind.FRACTION);
    }

    public static CountlangType integer() {
        return primitive(Kind.INT);
    }

    public static CountlangType bool() {
        return primitive(Kind.BOOL);
    }

    public static CountlangType any() {
        return primitive(Kind.ANY);
    }

    public static CountlangType distributionOf(CountlangType subType) {
        return compositeOf(Kind.DISTRIBUTION, subType);
    }

    private static CountlangType compositeOf(Kind compositeKind, CountlangType subType) {
        CountlangType key = new CountlangType(compositeKind, subType);
        if(! repository.containsKey(key)) {
            repository.put(key, key);
        }
        return repository.get(key);        
    }

    public static CountlangType distributionOfAny() {
        return distributionOf(any());
    }

    public static CountlangType arrayOf(CountlangType subType) {
        return compositeOf(Kind.ARRAY, subType);
    }

    public static CountlangType arrayOfAny() {
        return arrayOf(any());
    }

    public boolean isDistribution() {
        return kind == Kind.DISTRIBUTION;
    }

    public boolean isArray() {
        return kind == Kind.ARRAY;
    }

    public boolean isPrimitive() {
        return PRIMITIVES.contains(kind);
    }

    public boolean isPrimitiveNumeric() {
    	if( (kind == Kind.INT) || (kind == Kind.FRACTION)) {
    		return true;
    	}
    	return false;
    }

    public CountlangType getSubType() {
        return subType;
    }

    public List<CountlangType> getGeneralizations() {
        List<CountlangType> result = new ArrayList<>();
        if(isPrimitive()) {
            if(this != any()) {
                result.add(any());
            }
        } else if(isDistribution()) {
            for(CountlangType subTypeGeneralization: getSubType().getGeneralizations()) {
                result.add(distributionOf(subTypeGeneralization));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        if(kind.equals(Kind.ANY)) {
            return "?";
        }
        if(isPrimitive()) {
            return kind.name().toLowerCase();
        } else if(kind.equals(Kind.UNKNOWN)) {
            return "unknown";
        } else {
            return String.format("%s<%s>", kind.name().toLowerCase(), getSubType().toString());
        }
    }
}
