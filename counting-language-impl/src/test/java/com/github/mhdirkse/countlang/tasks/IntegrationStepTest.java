package com.github.mhdirkse.countlang.tasks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.steps.ExecutionPoint;
import com.github.mhdirkse.countlang.steps.Stepper;

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
