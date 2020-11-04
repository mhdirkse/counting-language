package com.github.mhdirkse.countlang.tasks;

import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;

import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.TestOutputStrategy;
import com.github.mhdirkse.countlang.types.Distribution;

public class IntegrationHappyTestBase implements OutputStrategy {
    TestOutputStrategy outputStrategy;

    @Before
    public void setUp() {
        StatusCode.setTestMode(true);
        outputStrategy = new TestOutputStrategy();
    }

    @After
    public void tearDown() {
        StatusCode.setTestMode(false);
    }

    @Override
    public void output(final String output) {
        outputStrategy.output(output);
    }

    @Override
    public void error(final String error) {
        outputStrategy.error(error);
    }

    static Distribution getSimpleDistribution() {
        Distribution.Builder builder = new Distribution.Builder();
        builder.add(1);
        builder.add(1);
        builder.add(3);
        return builder.build();
    }

    static Distribution getDistributionWithUnknown() {
        Distribution.Builder builder = new Distribution.Builder();
        builder.add(1);
        builder.addUnknown(2);
        return builder.build();
    }

    void compileAndRun(final String programText) {
        try {
            compileAndRunUnchecked(programText);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void compileAndRunUnchecked(final String programText) throws IOException {
        StringReader reader = new StringReader(programText);
        try {
            new ProgramExecutor(reader).run(this);
        }
        finally {
            reader.close();
        }
    }
}
