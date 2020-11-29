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

package com.github.mhdirkse.countlang.tasks;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.testhelper.AstConstructionTestBase;

@RunWith(EasyMockRunner.class)
public class VariableCheckTest extends AstConstructionTestBase {
    @Mock
    StatusReporter reporter;

    private VariableCheck instance;
    
    @Before
    public void setUp() {
        instance = VariableCheck.getInstance(reporter, new ArrayList<FunctionDefinitionStatement>());
    }

    private void execute() {
        ast.accept(instance);
        instance.listEvents();
    }

    @Test
    public void whenNoErrorThenNothingReported() {
        replay(reporter);
        parse("x = 5; print x");
        Assert.assertFalse(hasParseErrors);
        execute();
        verify(reporter);
    }

    @Test(expected = ProgramException.class)
    public void whenUndefinedVariableUsedThenException() {
        replay(reporter);
        parse("print xyz");
        Assert.assertFalse(hasParseErrors);
        execute();
    }

    @Test
    public void whenVariableNotUsedThenReported() {
        reporter.report(eq(StatusCode.VAR_NOT_USED), eq(1), anyInt(), eq("xyz"));
        replay(reporter);
        parse("xyz = 5; print 5");
        Assert.assertFalse(hasParseErrors);
        execute();
        verify(reporter);
    }

    @Test
    public void whenVariableIsFormalParameterThenUsed() {
        replay(reporter);
        parse("xyz = 5; function fun(int x) {return x}; print fun(xyz)");
        Assert.assertFalse(hasParseErrors);
        execute();
        verify(reporter);        
    }

    @Test
    public void testLocalAndGlobalVariablesAreDistinguished() {
        reporter.report(eq(StatusCode.VAR_NOT_USED), eq(1), anyInt(), eq("xyz"));
        replay(reporter);
        parse("xyz = 3; function fun(int abc) {print abc; xyz = 3; uvw = 5; print xyz; return uvw};");
        Assert.assertFalse(hasParseErrors);
        execute();
        verify(reporter);        
    }

    @Test
    public void testWhenVariableIsUpdatedUsingSelfThenNoError() {
        replay(reporter);
        parse("xyz = 3; xyz = xyz + 1; print xyz");
        Assert.assertFalse(hasParseErrors);
        execute();
        verify(reporter);        
    }
}
