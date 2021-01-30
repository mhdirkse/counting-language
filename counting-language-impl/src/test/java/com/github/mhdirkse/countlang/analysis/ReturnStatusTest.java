package com.github.mhdirkse.countlang.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

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
        blocks.handleReturn(1, 1);
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
        blocks.handleReturn(1, 1);
        blocks.stopBranch();
        blocks.stopSwitch();
        assertEquals(ReturnStatus.STRONG_ALL_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenSomeBranchReturnThenSomeReturn() {
        blocks.startSwitch();
        blocks.startBranch();
        blocks.handleReturn(1, 1);
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
        blocks.handleReturn(1, 1);
        blocks.stopRepetition();
        assertEquals(ReturnStatus.STRONG_ALL_RETURN, blocks.getLastAddedBlock().getReturnStatus());
    }

    @Test
    public void whenRepetitionHasAlwaysReturningBlocksThenAllReturn() {
        blocks.startRepetition();
        blocks.startSwitch();
        blocks.startBranch();
        blocks.handleReturn(1, 1);
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
        blocks.handleReturn(1, 1);
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
        blocks.handleReturn(1, 1);
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
        blocks.handleReturn(1, 1);
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
