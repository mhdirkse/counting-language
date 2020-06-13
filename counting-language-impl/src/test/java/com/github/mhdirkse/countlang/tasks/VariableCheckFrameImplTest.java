package com.github.mhdirkse.countlang.tasks;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mhdirkse.countlang.execution.StackFrameAccess;

import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class VariableCheckFrameImplTest {
    private static final int LINE = 2;
    private static final int COLUMN = 3;
    private static final int LINE2 = 4;
    private static final int COLUMN2 = 5;
    private static final int LINE3 = 6;
    private static final int COLUMN3 = 7;

    private static final String NAME = "xyz";

    @Mock(type = MockType.STRICT)
    public StatusReporter reporter;

    private VariableCheckFrameImpl instance;

    @Before
    public void setUp() {
        instance = new VariableCheckFrameImpl(StackFrameAccess.HIDE_PARENT);
    }

    @Test
    public void whenVariableDefinedAndUsedThenNothingReported() {
        replay(reporter);
        instance.define(NAME, LINE, COLUMN);
        instance.use(NAME, LINE2, COLUMN2);
        instance.report(reporter);
        verify(reporter);
    }

    @Test
    public void whenVariableDefinedButNotUsedThenReported() {
        reporter.report(StatusCode.VAR_NOT_USED, LINE, COLUMN, NAME);
        replay(reporter);
        instance.define(NAME, LINE, COLUMN);
        instance.report(reporter);
        verify(reporter);
    }

    @Test
    public void whenUndefinedVariableReferencedThenReported() {
        reporter.report(StatusCode.VAR_UNDEFINED, LINE, COLUMN, NAME);
        replay(reporter);
        instance.use(NAME, LINE, COLUMN);
        instance.report(reporter);
        verify(reporter);
    }

    @Test
    public void whenSameVariableDefinedTwiceButNotUsedThenOnlyFirstReported() {
        reporter.report(StatusCode.VAR_NOT_USED, LINE, COLUMN, NAME);
        replay(reporter);
        instance.define(NAME, LINE, COLUMN);
        instance.define(NAME, LINE2, COLUMN2);
        instance.report(reporter);
        verify(reporter);
    }

    @Test
    public void whenSameUndefinedVariableReferencedTwiceThenOnlyFirstReported() {
        reporter.report(StatusCode.VAR_UNDEFINED, LINE, COLUMN, NAME);
        replay(reporter);
        instance.use(NAME, LINE, COLUMN);
        instance.use(NAME, LINE2, COLUMN2);
        instance.report(reporter);
        verify(reporter);
    }

    @Test
    public void whenVariableChangedAndChangeNotUsedThenReported() {
        reporter.report(StatusCode.VAR_NOT_USED, LINE3, COLUMN3, NAME);
        replay(reporter);
        instance.define(NAME, LINE, COLUMN);
        instance.use(NAME, LINE2, COLUMN2);
        instance.define(NAME, LINE3, COLUMN3);
        instance.report(reporter);
        verify(reporter);
    }

    @Test
    public void whenVariableReusedThenNotReported() {
        replay(reporter);
        instance.define(NAME, LINE, COLUMN);
        instance.use(NAME, LINE2, COLUMN2);
        instance.use(NAME, LINE3, COLUMN3);
        instance.report(reporter);
        verify(reporter);
    }
}
