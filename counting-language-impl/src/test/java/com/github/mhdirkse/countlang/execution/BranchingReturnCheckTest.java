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
