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

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.CountlangType;

@RunWith(EasyMockRunner.class)
public class SymbolFrameTypeCheckTest {
    private static final StackFrameAccess DUMMY_STACK_FRAME_ACCESS = StackFrameAccess.SHOW_PARENT;
    private static final String SYMBOL = "x";

    @Mock
    private SymbolNotAccessibleHandler handler;

    private SymbolFrameTypeCheck instance;

    @Before
    public void setUp() {
        instance = new SymbolFrameTypeCheck(DUMMY_STACK_FRAME_ACCESS, handler);
    }

    @Test
    public void whenSymbolWrittenReadThenNoError() {
        EasyMock.replay(handler);
        instance.write(SYMBOL, CountlangType.INT, 1, 1);
        instance.read(SYMBOL, 2, 2);
        EasyMock.verify(handler);
    }

    @Test
    public void whenUnknownSymbolReadThenReported() {
        handler.notReadable(SYMBOL, 2, 3);
        EasyMock.replay(handler);
        instance.read(SYMBOL, 2, 3);
        EasyMock.verify(handler);
    }

    @Test
    public void whenSymbolTypeChangedThenReported() {
        handler.notWritable(SYMBOL, 2, 3);
        EasyMock.replay(handler);
        instance.write(SYMBOL, CountlangType.BOOL, 1, 1);
        instance.write(SYMBOL, CountlangType.INT, 2, 3);
        EasyMock.verify(handler);
    }

    @Test
    public void whenSystemWrittenTwiceWithSameTypeThenOk() {
        EasyMock.replay(handler);
        instance.write(SYMBOL, CountlangType.BOOL, 1, 1);
        instance.write(SYMBOL, CountlangType.BOOL, 2, 3);
        EasyMock.verify(handler);        
    }

    @Test(expected = IllegalStateException.class)
    public void whenNewSymbolIntroducedInSwitchThenError() {
        EasyMock.replay(handler);
        instance.onSwitchOpened();
        instance.write(SYMBOL, CountlangType.BOOL, 1, 1);
    }

    @Test
    public void whenAllSwitchesClosedThenNewSymbolCanBeWritten() {
        EasyMock.replay(handler);
        instance.onSwitchOpened();
        instance.onSwitchClosed();
        instance.write(SYMBOL, CountlangType.BOOL, 1, 1);
        EasyMock.verify(handler);
    }
}
