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
import org.junit.Before;
import org.junit.Test;

public class SymbolFrameExecuteTest {
    private static final StackFrameAccess DUMMY_ACCESS = StackFrameAccess.SHOW_PARENT;
    private static final String SYMBOL = "x";
    private static final Integer VALUE = Integer.valueOf(10);

    private SymbolFrame instance;

    @Before
    public void setUp() {
        instance = new SymbolFrame(DUMMY_ACCESS);
    }

    @Test
    public void testWhenSymbolWrittenThenSymbolCanBeRead() {
        instance.write(SYMBOL, VALUE, 1, 1);
        Object result = instance.read(SYMBOL, 2, 3);
        Assert.assertEquals(VALUE, result);
    }
}
