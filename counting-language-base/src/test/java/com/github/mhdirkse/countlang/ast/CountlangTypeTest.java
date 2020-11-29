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

import org.junit.Assert;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.CountlangType;

public class CountlangTypeTest {
    @Test
    public void testIntegerGetsTypeInt() {
        Assert.assertEquals(CountlangType.INT, CountlangType.typeOf(new Integer(1)));
    }

    @Test
    public void testCountlangTypeIntMatchesAnInt() {
        Assert.assertTrue(CountlangType.INT.matches(new Integer(1).getClass()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLongValueIsNotAcceptedInCountlang() {
        CountlangType.typeOf(new Long(1));
    }

    @Test
    public void testIntDoesNotMatchLongValue() {
        Assert.assertFalse(CountlangType.INT.matches(Long.class));
    }

    @Test
    public void testAllCountlangTypesHaveCorrectExample() {
        for(CountlangType t : CountlangType.values()) {
            if(t == CountlangType.UNKNOWN) {
                continue;
            }
            Assert.assertTrue(t.matches(t.getExample().getClass()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenTypeOfNullThenExceptionThrown() {
        CountlangType.typeOf(null);
    }
}
