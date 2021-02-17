/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

import com.github.mhdirkse.countlang.algorithm.OutputStrategy;

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
