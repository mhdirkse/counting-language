package com.github.mhdirkse.countlang.tasks;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.execution.OutputStrategy;

@RunWith(EasyMockRunner.class)
public class StatusReporterImplTest {
    private final int LINE = 2;
    private final int COL = 3;

    @Mock(type = MockType.NICE)
    public OutputStrategy output;

    public StatusReporterImpl instance;

    @Before
    public void setUp() {
        StatusCode.setTestMode(true);
        instance = new StatusReporterImpl(output);
    }

    @After
    public void tearDown() {
        StatusCode.setTestMode(false);        
    }

    @Test
    public void whenStatusIsReportedThenStatusWrittenToOutput() {
        output.error(StatusCode.TEST_LINE_STATUS_NO_EXTRA_ARGS.format(
                Integer.valueOf(LINE).toString(),
                Integer.valueOf(COL).toString()));
        replay(output);
        instance.report(StatusCode.TEST_LINE_STATUS_NO_EXTRA_ARGS, LINE, COL);
        verify(output);
    }

    @Test
    public void onlyWhenStatusReportedThenHasErrors() {
        Assert.assertFalse(instance.hasErrors());
        instance.report(StatusCode.TEST_LINE_STATUS_NO_EXTRA_ARGS, LINE, COL);
        Assert.assertTrue(instance.hasErrors());
    }
}
