package com.github.mhdirkse.countlang.tasks;

import static com.github.mhdirkse.countlang.tasks.StatusCode.FUNCTION_ALREADY_DEFINED;
import static com.github.mhdirkse.countlang.tasks.StatusCode.FUNCTION_ARGUMENT_COUNT_MISMATCH;
import static com.github.mhdirkse.countlang.tasks.StatusCode.FUNCTION_DOES_NOT_EXIST;
import static com.github.mhdirkse.countlang.tasks.StatusCode.FUNCTION_DOES_NOT_RETURN;
import static com.github.mhdirkse.countlang.tasks.StatusCode.FUNCTION_NESTED_NOT_ALLOWED;
import static com.github.mhdirkse.countlang.tasks.StatusCode.FUNCTION_STATEMENT_WITHOUT_EFFECT;
import static com.github.mhdirkse.countlang.tasks.StatusCode.RETURN_OUTSIDE_FUNCTION;
import static com.github.mhdirkse.countlang.tasks.StatusCode.VAR_NOT_USED;
import static com.github.mhdirkse.countlang.tasks.StatusCode.VAR_UNDEFINED;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.mhdirkse.utils.AbstractStatusCode;

/**
 * Check that all status codes are used with the right format specifiers.
 * From the test that check whether the right statusses are reported,
 * the expected StatusReporter.report statements were retrieved manually
 * and copied here. Then we test whether these supply enough arguments
 * to substitute all the format specifiers needed by the status code.
 * @author martijn
 *
 */
@RunWith(Parameterized.class)
public class StatusCodeUsageTest {
    @Before
    public void setUp() {
        StatusCode.setTestMode(true);
    }

    @After
    public void tearDown() {
        StatusCode.setTestMode(false);
    }

    @Parameters(name = "Status {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {FUNCTION_ALREADY_DEFINED, new String[] {"2", "1", "fun"}},
            {FUNCTION_ARGUMENT_COUNT_MISMATCH, new String[] {
                    "2", "1", "fun", "0", "1"}},
            {FUNCTION_DOES_NOT_EXIST, new String[] {"1", "2", "fun"}},
            {FUNCTION_DOES_NOT_RETURN, new String[] {"1", "1", "fun"}},
            {FUNCTION_NESTED_NOT_ALLOWED, new String[] {"1", "2"}},
            {FUNCTION_STATEMENT_WITHOUT_EFFECT, new String[] {"2", "1", "fun"}},
            {RETURN_OUTSIDE_FUNCTION, new String[] {"1", "1"}},
            {VAR_NOT_USED, new String[] {"1", "2", "xyz"}},
            {VAR_UNDEFINED, new String[] {"1", "2", "xyz"}}});
    }

    @Parameter
    public AbstractStatusCode statusCode;

    @Parameter(1)
    public String[] arguments;

    @Test
    public void testNoMissingFormatArguments() {
        statusCode.format(arguments);
    }
}
