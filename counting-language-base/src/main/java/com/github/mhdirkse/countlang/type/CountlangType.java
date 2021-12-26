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

package com.github.mhdirkse.countlang.type;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.EqualsAndHashCode;

/**
 * Entry point of creating counting-language types. Class {@link TupleType} can only be
 * constructed through the static factory method in this class.
 * @author martijn
 *
 */
// Tested in package com.github.mhdirkse.countlang.ast. Then we
// have no access to package-private stuff of package com.github.mhdirkse.countlang.type.
@EqualsAndHashCode
public class CountlangType {
    enum Kind {
        UNKNOWN,
        ANY,
        FRACTION,
        INT,
        BOOL,
        DISTRIBUTION,
        ARRAY,
        RANGE,
        TUPLE;
    }

    private static final Set<Kind> PRIMITIVES = EnumSet.of(Kind.FRACTION, Kind.INT, Kind.BOOL);

    private static final Map<CountlangType, CountlangType> repository = new HashMap<>();

    private final Kind kind;
    private final CountlangType subType;
    private final CountlangType secondSubType;

    private CountlangType(final Kind kind, final CountlangType subType) {
        this.kind = kind;
        this.subType = subType;
        this.secondSubType = null;
    }

    CountlangType(final Kind kind, final CountlangType subType, CountlangType secondSubType) {
        this.kind = kind;
        this.subType = subType;
        this.secondSubType = secondSubType;
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

    public static CountlangType rangeOf(CountlangType subType) {
    	if((subType != CountlangType.integer()) && (subType != CountlangType.fraction())) {
    		throw new IllegalArgumentException("Ranges can only be built from integers of fractions");
    	}
    	return compositeOf(Kind.RANGE, subType);
    }

    public static TupleType tupleOf(List<CountlangType> rawSubTypes) {
        List<CountlangType> subTypes = new ArrayList<>();
        for(CountlangType rawSubType: rawSubTypes) {
            if(rawSubType.isTuple()) {
                TupleType theTuple = (TupleType) rawSubType;
                subTypes.addAll(theTuple.getTupleSubTypes());
            } else {
                subTypes.add(rawSubType);
            }
        }
        if(subTypes.size() <= 1) {
            throw new IllegalArgumentException("Tuples should hold at least two members");
        }
        if(subTypes.size() == 2) {
            CountlangType key = new TupleType(subTypes.get(0), subTypes.get(1));
            if(! repository.containsKey(key)) {
                repository.put(key, key);
            }
            return (TupleType) repository.get(key);
        } else {
            CountlangType tail = tupleOf(subTypes.subList(1, subTypes.size()));
            CountlangType key = new TupleType(subTypes.get(0), tail);
            if(! repository.containsKey(key)) {
                repository.put(key, key);
            }
            return (TupleType) repository.get(key);
        }
    }

    public boolean isDistribution() {
        return kind == Kind.DISTRIBUTION;
    }

    public boolean isArray() {
        return kind == Kind.ARRAY;
    }

    public boolean isRange() {
    	return kind == Kind.RANGE;
    }

    public final boolean containsRange() {
    	return isRange() || anySubTypeContainsRange();
    }

    boolean anySubTypeContainsRange() {
    	boolean result = false;
    	if(subType != null) {
    		result = result || subType.containsRange();
    	}
    	if(secondSubType != null) {
    		result = result || secondSubType.containsRange();
    	}
    	return result;
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

    public boolean isTuple() {
        return kind == Kind.TUPLE;
    }

    /**
     * Do not use for tuples.
     */
    public CountlangType getSubType() {
        return subType;
    }

    public List<CountlangType> getGeneralizations() {
        List<CountlangType> result = new ArrayList<>();
        if(this != any()) {
            result.add(any());
        } 
        if(isDistribution()) {
            for(CountlangType subTypeGeneralization: getSubType().getGeneralizations()) {
                result.add(distributionOf(subTypeGeneralization));
            }
        }
        if(isArray()) {
            for(CountlangType subTypeGeneralization: getSubType().getGeneralizations()) {
                result.add(arrayOf(subTypeGeneralization));
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
        // We do not have to do tuples here, happens by TupleType.toString.
    }
}
