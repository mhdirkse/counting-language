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

import com.github.mhdirkse.countlang.algorithm.Distribution;

public enum CountlangType {
    UNKNOWN(Object.class, null),
    INT(Integer.class, new Integer(1)),
    BOOL(Boolean.class, new Boolean(true)),
    DISTRIBUTION(Distribution.class, new Distribution.Builder().build());
    
    private final Class<?> implementationClass;
    private final Object example;

    CountlangType(Class<?> implementationClass, Object example) {
        this.implementationClass = implementationClass;
        this.example = example;
    }

    public boolean matches(Class<?> clazz) {
        return implementationClass.isAssignableFrom(clazz); 
    }

    public Object getExample() {
        return example;
    }

    public static CountlangType typeOf(Object value) {
        if(value == null) {
            throw new IllegalArgumentException("Value null does not exist in the CountLang language");
        }
        for(CountlangType t: CountlangType.values()) {
            if(t == CountlangType.UNKNOWN) {
                continue;
            }
            if(t.implementationClass.isAssignableFrom(value.getClass())) {
                return t;
            }
        }
        throw new IllegalArgumentException(String.format(
                "Value has a type that is not supported by Countlang: %s",
                value.toString()));
    }
}
