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

package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ExecutionPointImplTest {
    @Test
    public void whenExecutionPointEmptyThenValid() {
        Assert.assertTrue(new ExecutionPointImpl(new ArrayList<>()).isValid());
    }

    @Test
    public void whenBeforeFollowedByRunningThenInvalid() {
        Assert.assertFalse(getInvalid().isValid());
    }

    private ExecutionPoint getInvalid() {
        ExecutionPoint instance = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, BEFORE),
                new ExecutionPointNode(null, RUNNING)));
        return instance;
    }

    @Test
    public void whenRunningFollowedByBeforeThenValid() {
        ExecutionPoint instance = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, RUNNING),
                new ExecutionPointNode(null, BEFORE)));
        Assert.assertTrue(instance.isValid());
    }

    @Test
    public void whenExecutionPointNonEndsWithTwoAfterThenInvalid() {
        ExecutionPoint instance = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, AFTER),
                new ExecutionPointNode(null, AFTER)));
        Assert.assertFalse(instance.isValid());
    }

    @Test(expected = IllegalStateException.class)
    public void whenEmptyExecutionPointThenCannotDetermineAfterFinished() {
        new ExecutionPointImpl(new ArrayList<>()).afterFinished();
    }

    @Test(expected = IllegalStateException.class)
    public void whenInvalidExecutionPointThenCannotDetermineAfterFinished() {
        ExecutionPointImpl instance = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, BEFORE),
                new ExecutionPointNode(null, RUNNING)));
        instance.afterFinished();
    }

    @Test
    public void whenSingleNodeInExecutionPointThenAfterFinishedTurnsNodeToAfter() {
        ExecutionPointImpl instance = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, BEFORE)));
        ExecutionPointImpl result = (ExecutionPointImpl) instance.afterFinished();
        List<AstNodeExecutionState> resultStates = result.getStates();
        Assert.assertEquals(1, resultStates.size());
        Assert.assertEquals(AFTER, resultStates.get(0));
    }

    @Test
    public void whenTwoNodesInExecutionPointThenAfterFinishedOnlyChangesLast() {
        ExecutionPointImpl instance = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, RUNNING),
                new ExecutionPointNode(null, BEFORE)));
        ExecutionPointImpl result = (ExecutionPointImpl) instance.afterFinished();
        List<AstNodeExecutionState> resultStates = result.getStates();
        Assert.assertEquals(2, resultStates.size());
        Assert.assertEquals(RUNNING, resultStates.get(0));
        Assert.assertEquals(AFTER, resultStates.get(1));
    }
}
