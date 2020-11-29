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

package com.github.mhdirkse.countlang.generator;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.codegen.compiletime.MethodModel;

import org.junit.Assert;

public class VisitorClassModelTest {
    private MethodModel methodModel;
    private MethodModel methodModelWithoutVisit;
    private MethodModel methodModelVisitNotAtStart;
    @Before
    public void setUp() {
        methodModel = new MethodModel();
        methodModel.setName("visitX");
        methodModelWithoutVisit = new MethodModel();
        methodModelWithoutVisit.setName("x");
        methodModelVisitNotAtStart = new MethodModel();
        methodModelVisitNotAtStart.setName("dovisit");
    }

    @Test
    public void whenNameStartsWithVisitThenGetListenerMethodReplacesWithPrefix() {
        Assert.assertEquals("enterX", VisitorClassModel.getListenerMethod(methodModel, "enter").getName());
    }

    @Test
    public void whenNameDoesNotStartWithVisitThenGetListenerMethodPrependsPrefix() {
        Assert.assertEquals("enterX",
                VisitorClassModel.getListenerMethod(methodModelWithoutVisit, "enter").getName());
    }

    @Test
    public void whenNameHasVisitInMiddleThenGetListenerMethodPrependsPrefix() {
        Assert.assertEquals("enterDovisit",
                VisitorClassModel.getListenerMethod(methodModelVisitNotAtStart, "enter").getName());
    }
}
