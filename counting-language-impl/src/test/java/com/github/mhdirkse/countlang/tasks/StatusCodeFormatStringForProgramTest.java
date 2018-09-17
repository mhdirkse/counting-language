package com.github.mhdirkse.countlang.tasks;

import static com.github.mhdirkse.countlang.tasks.StatusCode.TEST_LINE_STATUS_NO_EXTRA_ARGS;
import static com.github.mhdirkse.countlang.tasks.StatusCode.TEST_LINE_STATUS_ONE_EXTRA_ARG;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.mhdirkse.utils.AbstractStatusCode;

@RunWith(Parameterized.class)
public class StatusCodeFormatStringForProgramTest {
    private static Set<AbstractStatusCode> NOT_PROGRAM = new HashSet<>(Arrays.asList(
            TEST_LINE_STATUS_NO_EXTRA_ARGS,
            TEST_LINE_STATUS_ONE_EXTRA_ARG));

    @Parameters(name = "Enum value {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(StatusCode.values()).stream()
                .filter(s -> (!NOT_PROGRAM.contains(s)))
                .map(sc -> new Object[] {sc})
                .collect(Collectors.toList());
    }

    @Parameter
    public StatusCode statusCode;

    @Test
    public void testFormatStringStartsWithLineAndColumn() {
        Assert.assertThat(statusCode.getFormatString(), CoreMatchers.startsWith("({1}, {2})"));
    }
}
