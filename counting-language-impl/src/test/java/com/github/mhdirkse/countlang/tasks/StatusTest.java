package com.github.mhdirkse.countlang.tasks;

import org.junit.Assert;
import org.junit.Test;

public class StatusTest {
    @Test
    public void whenStatusFormatsOnlyLineAndColumnThenCorrectlyFormatted() {
        Assert.assertEquals("Test line 2 column 4.",
                Status.forLine(StatusCode.TEST_LINE_STATUS_NO_EXTRA_ARGS, 2, 4).format());
    }

    @Test
    public void whenStatusFormatsExtraArgumentsThenFormattedCorrectly() {
        Assert.assertEquals("Test line 3 column 1 with something.",
                Status.forLine(StatusCode.TEST_LINE_STATUS_ONE_EXTRA_ARG, 3, 1, "something").format());
    }
}
