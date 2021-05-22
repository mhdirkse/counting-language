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

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

public class ScopeStackTest {
    private ScopeStack<ScopeImpl> instance;

    @Before
    public void setUp() {
        instance = new ScopeStack<ScopeImpl>();
    }

    @Test(expected = IllegalStateException.class)
    public void whenEmptyStackQueriedThenError() {
        instance.findScope("someName");
    }

    @Test
    public void whenMultipleScopesHaveSymbolThenTopmostChosen() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        ScopeImpl top = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findScope("someName");
        assertSame(top, actual);
    }

    @Test
    public void whenTopScopeDoesNotHaveSymbolThenScopeHavingSymbolReturned() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        ScopeImpl top = new ScopeImpl(ScopeAccess.SHOW_PARENT);
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findScope("someName");
        assertSame(bottom, actual);        
    }

    @Test
    public void whenNoScopeHasSymbolThenTopReturned() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT);
        ScopeImpl top = new ScopeImpl(ScopeAccess.SHOW_PARENT);
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findScope("someName");
        assertSame(top, actual);        
    }

    @Test
    public void whenScopeHidesParentThenScopesBelowAreIgnored() {
        ScopeImpl bottom = new ScopeImpl(ScopeAccess.SHOW_PARENT, "someName");
        ScopeImpl top = new ScopeImpl(ScopeAccess.HIDE_PARENT);
        instance.push(bottom);
        instance.push(top);
        ScopeImpl actual = instance.findScope("someName");
        assertSame(top, actual);                
    }
}
