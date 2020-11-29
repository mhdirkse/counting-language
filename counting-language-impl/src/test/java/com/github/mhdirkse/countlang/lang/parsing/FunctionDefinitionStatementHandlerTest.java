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

package com.github.mhdirkse.countlang.lang.parsing;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.lang.CountlangParser;

@RunWith(EasyMockRunner.class)
public class FunctionDefinitionStatementHandlerTest {
    private FunctionDefinitionStatementHandler instance;

    @Mock(type = MockType.STRICT)
    private DelegationContext delegationContext;

    @Mock(type = MockType.STRICT)
    private CountlangParser.VarDeclsContext antlrContext;

    @Before
    public void setUp() {
        instance = new FunctionDefinitionStatementHandler(1, 1);
    }

    @Test
    public void whenExitVarDeclsAndFirstHandlerThenNotHandled() {
        expect(delegationContext.isFirst()).andReturn(true).anyTimes();
        replay(antlrContext);
        replay(delegationContext);
        Assert.assertFalse(instance.exitVarDecls(antlrContext, delegationContext));
        verify(antlrContext);
        verify(delegationContext);
    }
}
