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

package com.github.mhdirkse.countlang.execution;

import org.junit.Assert;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.CountlangType;

public class SymbolTest {
    @Test
    public void whenSymbolCreatedThenUnknownTypeAndValueNull() {
        Symbol s = new Symbol("x");
        Assert.assertEquals("x", s.getName());
        Assert.assertEquals(CountlangType.UNKNOWN, s.getCountlangType());
        Assert.assertNull(s.getValue());
    }

    @Test
    public void whenSymbolInitializedToIntThenTypeBecomesInt() {
        Symbol s = new Symbol("x");
        s.setValue(CountlangType.INT.getExample());
        Assert.assertEquals(CountlangType.INT, s.getCountlangType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSymbolInitializedToNonCountlangValueThenExceptionThrown() {
        Symbol s = new Symbol("x");
        s.setValue(new Long(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSymbolChangesTypeThenExceptionThrown() {
        Symbol s = new Symbol("x");
        s.setValue(CountlangType.BOOL.getExample());
        s.setValue(CountlangType.INT.getExample());
    }

    @Test(expected= NullPointerException.class)
    public void whenSymbolSetToNullThenError() {
        Symbol s = new Symbol("x");
        s.setValue(null);        
    }
}
