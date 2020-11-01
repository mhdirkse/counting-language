package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;

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

    @Test
    public void testBeforeSmallerThanAfter() {
        ExecutionPointImpl lesser = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, BEFORE)));
        ExecutionPointImpl greater = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, AFTER)));
        Assert.assertTrue(lesser.compareTo(greater) < 0);
        Assert.assertTrue(greater.compareTo(lesser) > 0);
        Assert.assertEquals(0, lesser.compareTo(lesser));
    }

    @Test
    public void testTwoTimesAfterIsSmallerThanOneTimeAfter() {
        ExecutionPointImpl lesser = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, AFTER),
                new ExecutionPointNode(null, AFTER)));
        ExecutionPointImpl greater = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, AFTER)));
        Assert.assertTrue(lesser.compareTo(greater) < 0);
        Assert.assertTrue(greater.compareTo(lesser) > 0);        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareWithInvalidProducesError() {
        ExecutionPointImpl instance = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, BEFORE)));
        instance.compareTo(getInvalid());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareInvalidProducesError() {
        ExecutionPointImpl instance = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, BEFORE)));
        getInvalid().compareTo(instance);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareWithEmptyProducesError() {
        ExecutionPointImpl instance = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, BEFORE)));
        ExecutionPointImpl empty = new ExecutionPointImpl(new ArrayList<>());
        instance.compareTo(empty);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareEmptyProducesError() {
        ExecutionPointImpl instance = new ExecutionPointImpl(Arrays.asList(
                new ExecutionPointNode(null, BEFORE)));
        ExecutionPointImpl empty = new ExecutionPointImpl(new ArrayList<>());
        empty.compareTo(instance);
    }
}
