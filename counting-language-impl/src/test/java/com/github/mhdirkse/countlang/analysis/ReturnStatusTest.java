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

package com.github.mhdirkse.countlang.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.ast.ValueReturnStatement;

public class ReturnStatusTest {
    private CodeBlocks blocks;

    @Before
    public void setUp() {
        blocks = new CodeBlocks(new MemoryImpl());
        blocks.start();
    }

    @Test
    public void whenEmptyProgramThenNoneReturns() {
        assertEquals(ReturnStatus.NONE_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenNoChildBlocksAndNoReturnThenNoneReturns() {
        blocks.handleStatement(1, 1);
        assertEquals(ReturnStatus.NONE_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenNoChildBlocksAndReturnThenStongAllReturn() {
        blocks.handleStatement(1, 1);
        blocks.handleReturn(1, 1, ValueReturnStatement.class);
        assertEquals(ReturnStatus.STRONG_ALL_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenNoBranchReturnsThenNoneReturns() {
        blocks.startSwitch();
        blocks.startBranch();
        blocks.stopBranch();
        blocks.stopSwitch();
        assertEquals(ReturnStatus.NONE_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenAllBranchReturnThenStrongAllReturn() {
        blocks.startSwitch();
        blocks.startBranch();
        blocks.handleReturn(1, 1, ValueReturnStatement.class);
        blocks.stopBranch();
        blocks.stopSwitch();
        assertEquals(ReturnStatus.STRONG_ALL_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenSomeBranchReturnThenSomeReturn() {
        blocks.startSwitch();
        blocks.startBranch();
        blocks.handleReturn(1, 1, ValueReturnStatement.class);
        blocks.stopBranch();
        blocks.startBranch();
        blocks.stopBranch();
        blocks.stopSwitch();
        assertEquals(ReturnStatus.SOME_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenRepetitionNeverReturnsThenNoneReturn() {
        blocks.startRepetition();
        blocks.stopRepetition();
        assertEquals(ReturnStatus.NONE_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenRepetitionHasReturnThenAllReturn() {
        blocks.startRepetition();
        blocks.handleReturn(1, 1, ValueReturnStatement.class);
        blocks.stopRepetition();
        assertEquals(ReturnStatus.STRONG_ALL_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenRepetitionHasAlwaysReturningBlocksThenAllReturn() {
        blocks.startRepetition();
        blocks.startSwitch();
        blocks.startBranch();
        blocks.handleReturn(1, 1, ValueReturnStatement.class);
        blocks.stopBranch();
        blocks.stopSwitch();
        blocks.stopRepetition();
        assertEquals(ReturnStatus.STRONG_ALL_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenRepetitonContainsOnlySomeReturningBlocksThenWeakAllReturn() {
        blocks.startRepetition();
        blocks.startSwitch();
        blocks.startBranch();
        blocks.handleReturn(1, 1, ValueReturnStatement.class);
        blocks.stopBranch();
        blocks.startBranch();
        blocks.stopBranch();
        blocks.stopSwitch();
        blocks.stopRepetition();
        assertEquals(ReturnStatus.WEAK_ALL_RETURN, blocks.getLastAddedBlock().getReturnStatus());        
    }

    @Test
    public void whenWeakAllReturningBlockFollowedByStatementThenOnlySomeReturn_1() {
        blocks.startRepetition();
        blocks.startSwitch();
        blocks.startBranch();
        blocks.handleReturn(1, 1, ValueReturnStatement.class);
        blocks.stopBranch();
        blocks.startBranch();
        blocks.stopBranch();
        blocks.stopSwitch();
        blocks.stopRepetition();
        blocks.handleStatement(2, 1);
        assertEquals(ReturnStatus.SOME_RETURN, blocks.getLastAddedBlock().getReturnStatus());                
    }

    @Test
    public void whenWeakAllReturningBlockFollowedByStatementThenOnlySomeReturn_2() {
        blocks.startSwitch();
        blocks.startBranch();

        blocks.startRepetition();
        blocks.startSwitch();
        blocks.startBranch();
        blocks.handleReturn(1, 1, ValueReturnStatement.class);
        blocks.stopBranch();
        blocks.startBranch();
        blocks.stopBranch();
        blocks.stopSwitch();
        blocks.stopRepetition();
        // Have a weak returning block here, going to demote it

        blocks.stopBranch();
        blocks.stopSwitch();
        blocks.handleStatement(1, 1);
        assertEquals(ReturnStatus.SOME_RETURN, blocks.getLastAddedBlock().getReturnStatus());                
    }
}
