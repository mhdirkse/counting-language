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

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import static com.github.mhdirkse.countlang.execution.BranchingReturnCheck.Status.*;

public class BranchingReturnCheckTest {
    private BranchingReturnCheckImpl instance;
    
    @Before
    public void setUp() {
        instance = new BranchingReturnCheckImpl();
    }

    @Test
    public void whenNoBranchingAndNoReturnThenStatusNoReturn() {
        Assert.assertEquals(NO_RETURN, instance.getStatus());
    }

    @Test
    public void whenNoBranchingAndReturnThenStatusAllReturn() {
        instance.onReturn();
        Assert.assertEquals(ALL_RETURN, instance.getStatus());
    }

    @Test
    public void whenNoBranchReturnsThenStatusNoReturn() {
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onBranchClosed();
        instance.onSwitchClosed();
        Assert.assertEquals(NO_RETURN, instance.getStatus());
    }

    @Test
    public void whenFirstBranchReturnsThenStatusSomeReturn() {
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onBranchClosed();
        instance.onSwitchClosed();
        Assert.assertEquals(SOME_RETURN, instance.getStatus());
    }

    @Test
    public void whenSecondBranchReturnsThenStatusSomeReturn() {
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onSwitchClosed();
        Assert.assertEquals(SOME_RETURN, instance.getStatus());
    }

    @Test
    public void whenBothBranchesReturnThenStatusAllReturn() {
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onSwitchClosed();
        Assert.assertEquals(ALL_RETURN, instance.getStatus());
    }

    @Test
    public void whenSomeBranchesReturnFollwedByUnbranchedReturnThenAllReturn() {
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onBranchClosed();
        instance.onSwitchClosed();
        instance.onReturn();
        Assert.assertEquals(ALL_RETURN, instance.getStatus());
    }

    @Test
    public void whenInBranchThenBranchStatusReturned() {
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onReturn();
        Assert.assertEquals(ALL_RETURN, instance.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void whenSwitchingStartsAgainAfterAllReturnThenNotSupported() {
        instance.onReturn();
        instance.onSwitchOpened();
    }

    @Test
    public void whenSwitchingStartsAgainAfterSomeReturnThenNoError() {
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onBranchClosed();
        instance.onSwitchClosed();
        instance.onSwitchOpened();
        instance.onBranchOpened();
        Assert.assertEquals(NO_RETURN, instance.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void whenInBetweenBranchesThenAskingStatusIsNotSupported() {
        instance.onSwitchOpened();
        instance.getStatus();
    }

    @Test
    public void whenNesteBranchesAllReturnThenStatusAllReturn() {
        instance.onSwitchOpened();
        instance.onBranchOpened();

        // Inner switch
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onSwitchClosed();

        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onSwitchClosed();
        Assert.assertEquals(ALL_RETURN, instance.getStatus());
    }

    @Test
    public void whenOnlySecondSwitchHasAllBranchesReturningThenAllReturn() {
        // First switch
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onBranchClosed();
        instance.onSwitchClosed();
        
        // Second branch
        instance.onSwitchOpened();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onBranchOpened();
        instance.onReturn();
        instance.onBranchClosed();
        instance.onSwitchClosed();

        Assert.assertEquals(ALL_RETURN, instance.getStatus());
    }
    
}
