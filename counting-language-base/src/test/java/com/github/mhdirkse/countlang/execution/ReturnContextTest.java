package com.github.mhdirkse.countlang.execution;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReturnContextTest {
    private ReturnContext instanceNoReturn;
    private ReturnContext instanceWithReturn;

    @Before
    public void setUp() {
        instanceNoReturn = new ReturnContext(1, 1, false);
        instanceWithReturn = new ReturnContext(1, 1, true);
    }

    @Test
    public void whenBlockWithoutReturnThenContinueExecution() {
        Assert.assertTrue(instanceNoReturn.shouldContinue());
        Assert.assertFalse(instanceNoReturn.haveReturnValue());
    }

    @Test
    public void whenBlockWithReturnThenReturnHandled() {
        Assert.assertTrue(instanceWithReturn.shouldContinue());
        Assert.assertFalse(instanceWithReturn.haveReturnValue());
        instanceWithReturn.setReturnValue(Integer.valueOf(2));
        Assert.assertFalse(instanceWithReturn.shouldContinue());
        Assert.assertTrue(instanceWithReturn.haveReturnValue());
        int actualReturnValue = ((Integer) instanceWithReturn.getReturnValue()).intValue();
        Assert.assertEquals(2, actualReturnValue);
    }

    @Test(expected = ProgramException.class)
    public void whenBlockWithoutReturnThenErrorOnReturn() {
        instanceNoReturn.setReturnValue(Integer.valueOf(2));
    }

    @Test(expected = ProgramException.class)
    public void whenReturnedTwiceThenError() {
        instanceWithReturn.setReturnValue(Integer.valueOf(1));
        instanceWithReturn.setReturnValue(Integer.valueOf(2));
    }

    @Test(expected = ProgramException.class)
    public void whenReturnValueNullThenError() {
        instanceWithReturn.setReturnValue(null);
    }

    @Test(expected = ProgramException.class)
    public void whenNotApplicableReturnValueRequestedThenError() {
        instanceNoReturn.getReturnValue();
    }

    @Test(expected = ProgramException.class)
    public void whenNotYetAvailableReturnValueRequestedThenError() {
        instanceWithReturn.getReturnValue();
    }
}
