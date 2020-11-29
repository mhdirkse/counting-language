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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StatusTest {
    @Before
    public void setUp() {
        StatusCode.setTestMode(true);
    }

    @After
    public void tearDown() {
        StatusCode.setTestMode(false);
    }

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
