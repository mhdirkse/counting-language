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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.algorithm.OutputStrategy;
import com.github.mhdirkse.countlang.execution.ExecutionPoint;
import com.github.mhdirkse.countlang.execution.Stepper;

public class IntegrationStepTest implements OutputStrategy {
    private List<String> outputs;
    private List<String> errors;

    @Override
    public void output(String s) {
        outputs.add(s);
    }

    @Override
    public void error(String s) {
        errors.add(s);
    }

    @Before
    public void setUp() {
        outputs = new ArrayList<>();
        errors = new ArrayList<>();
    }

    @Test
    public void testCanUseExecutionPointToPauseExecution() {
        Stepper stepper = Utils.compileAndGetStepper("print 3; print 5", this);
        Assert.assertEquals(0, outputs.size());
        Assert.assertEquals(0, errors.size());
        stepper.step();
        ExecutionPoint beforeFirstPrint = stepper.getExecutionPoint();
        ExecutionPoint afterFirstPrint = beforeFirstPrint.afterFinished();
        stepper.runUntil(afterFirstPrint);
        Assert.assertEquals(1, outputs.size());
        Assert.assertEquals("3", outputs.get(0));
        Assert.assertEquals(0, errors.size());
    }
}
